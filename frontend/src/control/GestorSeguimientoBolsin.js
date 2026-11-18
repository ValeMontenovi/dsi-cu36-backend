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
//     También busca el mail y notifica (buscarMail, llamarCU31).
//
//   • FRONTEND (este archivo): recibe el pedido de la pantalla,
//     le pregunta al backend por HTTP y coordina la parte visual
//     (solicitarMapa con la InterfazGoogleMaps, la selección del
//     bolsín y la confirmación del envío del mail).
//
// Flujos alternativos del CU 36 que resuelve:
//   A1 → la CM no tiene bolsines enviados: no se pide elegir ninguno.
//   A3 → el GPS no informó la ubicación de ningún bolsín: tampoco.
//   A5 → el actor elige no enviar el mail: termina sin notificar.
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
    // del usuario, así que se toma del primero. Si no hay bolsines (A1), se usa
    // la CM a la que está asignado el usuario que inició sesión.
    const cmUsuario =
      bolsines.length > 0 ? bolsines[0].cmOrigen : sesion.getUsuarioLogueado().getComisionMedica().nombre
    this.pantalla.mostrarCMUsuario(cmUsuario)

    this.bolsines = bolsines
    this.solicitarMapa(bolsines)

    // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : solicitarSelBolsinTipo()
    // Solo se puede elegir un bolsín con ubicación: sin bolsines (A1) o sin
    // ninguna ubicación del GPS (A3) no hay nada para notificar.
    if (bolsines.some((bolsin) => bolsin.ubicacion)) {
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
  // confirma = true  → el actor quiere enviar el mail.
  // confirma = false → flujo alternativo A5: no se envía y termina el caso de uso.
  async regConfirmacion(confirma) {
    if (!confirma) {
      this.finCU(false)
      return
    }

    try {
      // En el backend: buscarMail() → llamarCU31/NotificarUbicacionBolsin()
      await pedirNotificacionUbicacion(this.sesion.getUsuario(), this.bolsinSeleccionado.numeroBolsin)
    } catch (error) {
      // Flujo alternativo: el backend no pudo enviar el mail.
      this.pantalla.mostrarErrorNotificacion(error.message)
      return
    }

    this.finCU(true)
  }

  // Diagrama: GestorSeguimientoBolsin → GestorSeguimientoBolsin : FinCU()
  finCU(mailEnviado) {
    // Aviso a la pantalla (no figura en el diagrama) para que muestre cómo terminó.
    this.pantalla.mostrarFinCasoDeUso(this.bolsinSeleccionado, mailEnviado)
  }
}
