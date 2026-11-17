import InterfazGoogleMaps from '../boundary/InterfazGoogleMaps'
import { pedirNotificacionUbicacion, pedirSeguimientoBolsines } from '../servicios/backendSeguimiento'

// =====================================================================
// CONTROL: GestorSeguimientoBolsin   (parte del navegador)
// ---------------------------------------------------------------------
// En el diagrama de secuencia hay UN solo GestorSeguimientoBolsin.
// Como la aplicación es web, ese gestor quedó repartido en dos partes:
//
//   • BACKEND (Java): busca la CM del usuario, los bolsines enviados
//     y su ubicación con el PATRÓN ADAPTER
//     (buscarCMUsuario, buscarBolsinesEnviadoCM, localizarBolsines).
//     También busca los mails y notifica (buscarMail, llamarCU31).
//
//   • FRONTEND (este archivo): recibe el pedido de la pantalla,
//     le pregunta al backend por HTTP y coordina la parte visual
//     (solicitarMapa con la InterfazGoogleMaps, la selección del
//     bolsín y la confirmación del envío del mail).
// =====================================================================

export default class GestorSeguimientoBolsin {
  constructor(pantalla) {
    this.pantalla = pantalla // PantallaSeguimientoBolsin: a quién le responde el gestor
    this.interfazGoogleMaps = new InterfazGoogleMaps()
    this.sesion = null // Activa:Sesion, se guarda para usarla en regConfirmacion()
    this.bolsines = [] // bolsines enviados que se mostraron en pantalla
    this.bolsinSeleccionado = null // Seleccionado:Bolsin
  }

  // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : registrarSeguimientoBolsin()
  async registrarSeguimientoBolsin(sesion) {
    this.sesion = sesion
    this.bolsinSeleccionado = null
    let bolsines
    try {
      // getUsuario() a la sesión activa. Con ese usuario el backend hace
      // buscarCMUsuario(), buscarBolsinesEnviadoCM() y localizarBolsines() (Adapter).
      bolsines = await pedirSeguimientoBolsines(sesion.getUsuario())
    } catch (error) {
      // Flujo alternativo: el backend respondió con error o no está levantado.
      this.pantalla.mostrarError(error.message)
      return
    }

    // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : mostrarCMUsuario()
    // El backend no manda la CM por separado: todos los bolsines salen de la CM
    // del usuario, así que se toma del primero. Si no hay bolsines, no se conoce.
    const cmUsuario = bolsines.length > 0 ? bolsines[0].cmOrigen : null
    this.pantalla.mostrarCMUsuario(cmUsuario)

    this.bolsines = bolsines
    this.solicitarMapa(bolsines)

    // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : solicitarSelBolsinTipo()
    // Si la CM no tiene bolsines enviados no hay nada para elegir.
    if (bolsines.length > 0) {
      this.pantalla.solicitarSelBolsinTipo()
    }
  }

  // Diagrama: GestorSeguimientoBolsin → GestorSeguimientoBolsin : solicitarMapa()
  solicitarMapa(bolsines) {
    // Diagrama: GestorSeguimientoBolsin → InterfazGoogleMaps : recibirUbicacionBolsin()
    const mapa = this.interfazGoogleMaps.recibirUbicacionBolsin(bolsines)

    // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : mostrarMapaBolsin()
    this.pantalla.mostrarMapaBolsin(bolsines, mapa)
  }

  // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : tomarSelecBolsin()
  // Recibe el número del bolsín que eligió el actor.
  tomarSelecBolsin(numeroBolsin) {
    this.bolsinSeleccionado = this.bolsines.find((bolsin) => bolsin.numeroBolsin === numeroBolsin)

    // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : solicitarEnvioMail()
    this.pantalla.solicitarEnvioMail(this.bolsinSeleccionado)
  }

  // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : regConfirmacion()
  async regConfirmacion() {
    try {
      // En el backend: buscarMail() → Bucle [Mientras haya Empleados] → llamarCU31/NotificarUbicacionBolsin()
      await pedirNotificacionUbicacion(this.sesion.getUsuario(), this.bolsinSeleccionado.numeroBolsin)
    } catch (error) {
      // Flujo alternativo: el backend no pudo enviar el mail (hoy responde 501).
      this.pantalla.mostrarErrorNotificacion(error.message)
      return
    }

    this.finCU()
  }

  // Diagrama: GestorSeguimientoBolsin → GestorSeguimientoBolsin : FinCU()
  finCU() {
    // Aviso a la pantalla (no figura en el diagrama) para que muestre que el envío terminó.
    this.pantalla.mostrarNotificacionEnviada(this.bolsinSeleccionado)
  }
}
