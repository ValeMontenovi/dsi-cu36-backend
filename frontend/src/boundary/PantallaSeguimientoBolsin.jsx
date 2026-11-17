import { Component } from 'react'
import GestorSeguimientoBolsin from '../control/GestorSeguimientoBolsin'
import TablaBolsines from '../vistas/TablaBolsines'
import MapaBolsines from '../vistas/MapaBolsines'
import SeleccionBolsin from '../vistas/SeleccionBolsin'
import ConfirmacionEnvioMail from '../vistas/ConfirmacionEnvioMail'
import BarraSuperior from '../vistas/BarraSuperior'

// Estado de la selección del bolsín y del envío del mail (todo vacío al empezar)
const SELECCION_VACIA = {
  seleccionHabilitada: false,
  numeroElegido: null,
  bolsinAConfirmar: null,
  enviando: false,
  errorNotificacion: null,
  notificacionEnviada: null,
}

// =====================================================================
// BOUNDARY: PantallaSeguimientoBolsin
// ---------------------------------------------------------------------
// Es la pantalla con la que interactúa el actor: el Encargado de Bolsín.
// Está escrita como CLASE para que sus métodos tengan los mismos nombres
// que los mensajes del diagrama de secuencia.
//
// Recibe (this.props):
//   sesion          → la Sesion activa del usuario que inició sesión
//   onVolverAlMenu  → vuelve al menú principal
//   onCerrarSesion  → cierra la sesión (CU 6)
//
// Qué guarda la pantalla (this.state):
//   habilitada → true después de habilitarPantalla()
//   cmUsuario  → nombre de la CM del usuario (lo manda mostrarCMUsuario)
//   bolsines   → bolsines enviados con su ubicación (null = todavía no llegaron)
//   mapa       → lo que armó la InterfazGoogleMaps para dibujar
//   error      → mensaje si algo salió mal (flujo alternativo)
//
//   seleccionHabilitada → true después de solicitarSelBolsinTipo()
//   numeroElegido       → bolsín que el actor marcó en la lista
//   bolsinAConfirmar    → bolsín que manda solicitarEnvioMail() (abre la confirmación)
//   enviando            → true mientras el backend envía el mail
//   errorNotificacion   → mensaje si no se pudo enviar el mail (flujo alternativo)
//   notificacionEnviada → bolsín notificado al terminar el caso de uso (FinCU)
// =====================================================================

export default class PantallaSeguimientoBolsin extends Component {
  constructor(props) {
    super(props)
    // La pantalla conoce a su gestor, y el gestor conoce a la pantalla (le pasamos "this")
    this.gestor = new GestorSeguimientoBolsin(this)
    this.state = {
      habilitada: false,
      cmUsuario: null,
      bolsines: null,
      mapa: null,
      error: null,
      ...SELECCION_VACIA,
    }
  }

  // React llama a este método apenas la pantalla aparece.
  // El actor ya eligió la opción en el menú principal, así que el caso de uso arranca solo.
  componentDidMount() {
    this.registrarSeguimientoBolsin()
  }

  // Diagrama: EncargadoDeBolsin → PantallaSeguimientoBolsin : registrarSeguimientoBolsin()
  // Se ejecuta cuando el actor elige la opción en el menú principal.
  registrarSeguimientoBolsin() {
    this.habilitarPantalla()

    // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : registrarSeguimientoBolsin()
    // Se le pasa la sesión activa del usuario que inició sesión (Activa:Sesion).
    this.gestor.registrarSeguimientoBolsin(this.props.sesion)
  }

  // Diagrama: PantallaSeguimientoBolsin → PantallaSeguimientoBolsin : habilitarPantalla()
  // Muestra la pantalla y borra lo que haya quedado de una consulta anterior.
  habilitarPantalla() {
    this.setState({ habilitada: true, cmUsuario: null, bolsines: null, mapa: null, error: null, ...SELECCION_VACIA })
  }

  // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : mostrarCMUsuario()
  mostrarCMUsuario(cmUsuario) {
    this.setState({ cmUsuario })
  }

  // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : mostrarMapaBolsin()
  // Recibe los bolsines (para la tabla) y el mapa que armó la InterfazGoogleMaps.
  mostrarMapaBolsin(bolsines, mapa) {
    this.setState({ bolsines, mapa })
  }

  // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : solicitarSelBolsinTipo()
  // Habilita la elección de un bolsín en la lista.
  solicitarSelBolsinTipo() {
    this.setState({ seleccionHabilitada: true })
  }

  // El actor marca un bolsín en la lista (todavía no confirma la elección).
  elegirBolsin(numeroBolsin) {
    this.setState({ numeroElegido: numeroBolsin, errorNotificacion: null })
  }

  // Diagrama: EncargadoDeBolsin → PantallaSeguimientoBolsin : tomarSeleccionBolsin()
  // Se ejecuta cuando el actor aprieta "Continuar" con un bolsín marcado.
  tomarSeleccionBolsin() {
    // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : tomarSelecBolsin()
    this.gestor.tomarSelecBolsin(this.state.numeroElegido)
  }

  // Diagrama: GestorSeguimientoBolsin → PantallaSeguimientoBolsin : solicitarEnvioMail()
  // Abre la ventana que pregunta si se envía el mail con la ubicación.
  solicitarEnvioMail(bolsin) {
    this.setState({ bolsinAConfirmar: bolsin, errorNotificacion: null })
  }

  // Diagrama: EncargadoDeBolsin → PantallaSeguimientoBolsin : tomarConfirmacion()
  tomarConfirmacion() {
    this.setState({ enviando: true })

    // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : regConfirmacion()
    this.gestor.regConfirmacion()
  }

  // Flujo alternativo: el actor no confirma el envío y vuelve a la selección.
  cancelarEnvio() {
    this.setState({ bolsinAConfirmar: null })
  }

  // Fin del caso de uso (el gestor ejecutó FinCU()): el mail se envió.
  mostrarNotificacionEnviada(bolsin) {
    this.setState({ ...SELECCION_VACIA, notificacionEnviada: bolsin })
  }

  // Flujo alternativo: el backend no pudo enviar el mail.
  mostrarErrorNotificacion(mensaje) {
    this.setState({ bolsinAConfirmar: null, enviando: false, errorNotificacion: mensaje })
  }

  // Flujo alternativo (no está en el diagrama principal): el backend respondió con error.
  mostrarError(mensaje) {
    this.setState({ error: mensaje })
  }

  // render() dibuja la pantalla según lo que haya en this.state.
  // React lo vuelve a ejecutar solo cada vez que se llama a this.setState(...).
  render() {
    const { habilitada, cmUsuario, bolsines, mapa, error } = this.state
    const { seleccionHabilitada, numeroElegido, bolsinAConfirmar, enviando, errorNotificacion, notificacionEnviada } =
      this.state
    const consultando = habilitada && bolsines === null && error === null
    // Pin que se resalta en el mapa: el elegido en la lista o el que se notificó
    const numeroResaltado = seleccionHabilitada ? numeroElegido : (notificacionEnviada?.numeroBolsin ?? null)
    // Solo para el resumen que se muestra arriba de la lista (no cambia la lógica)
    const ubicados = bolsines ? bolsines.filter((bolsin) => bolsin.ubicacion).length : 0

    return (
      <div className="app">
        {/* Barra superior: nombre de la pantalla, usuario logueado y navegación */}
        <BarraSuperior titulo="Bolsines" subtitulo="Seguimiento de bolsines" sesion={this.props.sesion}>
          <button className="boton-barra" onClick={() => this.props.onVolverAlMenu()} disabled={enviando}>
            Menú principal
          </button>
          <button className="boton-barra" onClick={() => this.props.onCerrarSesion()} disabled={enviando}>
            Cerrar sesión
          </button>
        </BarraSuperior>

        <main className="pantalla">
          {/* Hasta que no se llama a habilitarPantalla() no se muestra nada más */}
          {habilitada && (
            <section className="resultado">
              {/* Mientras consulta todavía no se conoce la CM, así que el resumen se oculta */}
              {!consultando && (
                <div className="resumen">
                  <p className="cm-usuario">
                    Comisión médica del usuario: <strong>{cmUsuario ?? '—'}</strong>
                  </p>
                  {bolsines && bolsines.length > 0 && (
                    <p className="conteo">
                      {bolsines.length} {bolsines.length === 1 ? 'bolsín enviado' : 'bolsines enviados'},{' '}
                      {ubicados} con ubicación GPS
                    </p>
                  )}
                </div>
              )}

              {consultando && (
                <div className="cargando" role="status">
                  <div className="cargando-ruta" aria-hidden="true">
                    <span />
                  </div>
                  <p>Consultando la ubicación de los bolsines en el GPS…</p>
                </div>
              )}

              {error && (
                <div className="alerta" role="alert">
                  <strong>No se pudo consultar el seguimiento.</strong>
                  <p>{error}</p>
                </div>
              )}

              {bolsines && bolsines.length === 0 && (
                <p className="vacio">La comisión médica no tiene bolsines enviados.</p>
              )}

              {bolsines && bolsines.length > 0 && (
                <div className="contenido">
                  <TablaBolsines
                    bolsines={bolsines}
                    seleccionHabilitada={seleccionHabilitada}
                    numeroElegido={numeroElegido}
                    onElegir={(numeroBolsin) => this.elegirBolsin(numeroBolsin)}
                  />
                  <MapaBolsines mapa={mapa} numeroElegido={numeroResaltado} />
                </div>
              )}

              {/* Después de solicitarSelBolsinTipo(): barra para elegir y continuar */}
              {bolsines && bolsines.length > 0 && (seleccionHabilitada || notificacionEnviada) && (
                <SeleccionBolsin
                  bolsines={bolsines}
                  numeroElegido={numeroElegido}
                  errorNotificacion={errorNotificacion}
                  notificacionEnviada={notificacionEnviada}
                  onContinuar={() => this.tomarSeleccionBolsin()}
                />
              )}
            </section>
          )}
        </main>

        {/* Después de solicitarEnvioMail(): ventana de confirmación */}
        {bolsinAConfirmar && (
          <ConfirmacionEnvioMail
            bolsin={bolsinAConfirmar}
            enviando={enviando}
            onConfirmar={() => this.tomarConfirmacion()}
            onCancelar={() => this.cancelarEnvio()}
          />
        )}
      </div>
    )
  }
}
