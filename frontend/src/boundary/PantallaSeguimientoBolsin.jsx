import { Component } from 'react'
import GestorSeguimientoBolsin from '../control/GestorSeguimientoBolsin'
import TablaBolsines from '../vistas/TablaBolsines'
import MapaBolsines from '../vistas/MapaBolsines'
import FiltroBolsines from '../vistas/FiltroBolsines'
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
  finCasoDeUso: null,
}

// Filtros por precinto y CM destino (vacíos = se ven todos)
const FILTROS_VACIOS = { filtroPrecinto: '', filtroDestino: '' }

// ¿El precinto del bolsín contiene lo que escribió el actor? (sin distinguir mayúsculas)
function coincidePrecinto(bolsin, texto) {
  return bolsin.numeroPrecinto.toUpperCase().includes(texto.trim().toUpperCase())
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
//   filtroPrecinto, filtroDestino → filtros que escribe el actor (A2)
//
//   seleccionHabilitada → true después de solicitarSelBolsinTipo()
//   numeroElegido       → bolsín que el actor marcó en la lista o en el mapa
//   bolsinAConfirmar    → bolsín que manda solicitarEnvioMail() (abre la confirmación)
//   enviando            → true mientras el backend envía el mail
//   errorNotificacion   → mensaje si no se pudo enviar el mail (flujo alternativo)
//   finCasoDeUso        → { bolsin, mailEnviado } cuando el gestor ejecutó FinCU()
//
// Flujos alternativos del CU 36 (ERS):
//   A1 → no se encuentran bolsines en estado Enviado
//   A2 → no se encuentra bolsín con el número de precinto ingresado
//   A3 → el GPS Tracker no pudo informar la ubicación del bolsín
//   A4 → el actor cancela el caso de uso (botón "Cancelar")
//   A5 → el actor no quiere enviar el mail (botón "No enviar")
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
      ...FILTROS_VACIOS,
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
    this.setState({
      habilitada: true,
      cmUsuario: null,
      bolsines: null,
      mapa: null,
      error: null,
      ...FILTROS_VACIOS,
      ...SELECCION_VACIA,
    })
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
  // Habilita la elección de un bolsín en la lista o en el mapa.
  solicitarSelBolsinTipo() {
    this.setState({ seleccionHabilitada: true })
  }

  // Filtros del paso 6 de la ERS: por número de precinto o por CM destino.
  // Si el bolsín marcado queda oculto por el filtro, se desmarca.
  filtrarBolsines(cambios) {
    this.setState((estado) => {
      const filtros = { filtroPrecinto: estado.filtroPrecinto, filtroDestino: estado.filtroDestino, ...cambios }
      const sigueVisible = this.bolsinesVisibles(estado.bolsines ?? [], filtros).some(
        (bolsin) => bolsin.numeroBolsin === estado.numeroElegido,
      )
      return { ...cambios, numeroElegido: sigueVisible ? estado.numeroElegido : null }
    })
  }

  bolsinesVisibles(bolsines, { filtroPrecinto, filtroDestino }) {
    return bolsines
      .filter((bolsin) => !filtroPrecinto.trim() || coincidePrecinto(bolsin, filtroPrecinto))
      .filter((bolsin) => !filtroDestino || bolsin.cmDestino === filtroDestino)
  }

  // El actor marca un bolsín en la lista o toca su pin (todavía no confirma la elección).
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
  // confirma = true con "Enviar mail"; false con "No enviar" (flujo alternativo A5).
  tomarConfirmacion(confirma) {
    if (confirma) this.setState({ enviando: true })

    // Diagrama: PantallaSeguimientoBolsin → GestorSeguimientoBolsin : regConfirmacion()
    this.gestor.regConfirmacion(confirma)
  }

  // El actor cierra la ventana para elegir otro bolsín.
  cancelarEnvio() {
    this.setState({ bolsinAConfirmar: null })
  }

  // Flujo alternativo A4: el actor cancela el caso de uso y vuelve al menú.
  cancelarCasoDeUso() {
    this.props.onVolverAlMenu()
  }

  // Fin del caso de uso (el gestor ejecutó FinCU()), con o sin mail enviado.
  mostrarFinCasoDeUso(bolsin, mailEnviado) {
    this.setState({ ...SELECCION_VACIA, finCasoDeUso: { bolsin, mailEnviado } })
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
    const { habilitada, cmUsuario, bolsines, mapa, error, filtroPrecinto, filtroDestino } = this.state
    const { seleccionHabilitada, numeroElegido, bolsinAConfirmar, enviando, errorNotificacion, finCasoDeUso } =
      this.state
    const consultando = habilitada && bolsines === null && error === null
    const hayBolsines = Boolean(bolsines && bolsines.length > 0)
    const ubicados = bolsines ? bolsines.filter((bolsin) => bolsin.ubicacion).length : 0

    // Lo que queda después de aplicar los filtros (A2 si no queda ninguno)
    const visibles = hayBolsines ? this.bolsinesVisibles(bolsines, { filtroPrecinto, filtroDestino }) : []
    const numerosVisibles = new Set(visibles.map((bolsin) => bolsin.numeroBolsin))
    const mapaVisible = mapa && {
      ...mapa,
      marcadores: mapa.marcadores.filter((marcador) => numerosVisibles.has(marcador.numeroBolsin)),
    }
    const destinos = hayBolsines ? [...new Set(bolsines.map((bolsin) => bolsin.cmDestino))].sort() : []

    // Pin que se resalta en el mapa: el elegido o el del final del caso de uso
    const numeroResaltado = seleccionHabilitada ? numeroElegido : (finCasoDeUso?.bolsin?.numeroBolsin ?? null)

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
              {!consultando && !error && (
                <div className="resumen">
                  <p className="cm-usuario">
                    Comisión médica del usuario: <strong>{cmUsuario ?? '—'}</strong>
                  </p>
                  {hayBolsines && (
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

              {/* A1: no se encuentran bolsines en estado Enviado */}
              {bolsines && bolsines.length === 0 && (
                <div className="vacio">
                  <strong>No se encontraron bolsines en estado Enviado.</strong>
                  <p>La Comisión Médica {cmUsuario} no tiene bolsines en viaje para seguir.</p>
                  <button className="boton-secundario" onClick={() => this.props.onVolverAlMenu()}>
                    Volver al menú principal
                  </button>
                </div>
              )}

              {/* A3: el GPS no informó la ubicación de ningún bolsín */}
              {hayBolsines && ubicados === 0 && (
                <div className="alerta" role="alert">
                  <strong>El GPS Tracker no pudo informar la ubicación de ningún bolsín.</strong>
                  <p>Sin ubicación no hay nada para notificar. Probá de nuevo en unos minutos.</p>
                </div>
              )}

              {hayBolsines && (
                <FiltroBolsines
                  filtroPrecinto={filtroPrecinto}
                  filtroDestino={filtroDestino}
                  destinos={destinos}
                  cantidadVisible={visibles.length}
                  cantidadTotal={bolsines.length}
                  onCambiarPrecinto={(texto) => this.filtrarBolsines({ filtroPrecinto: texto })}
                  onCambiarDestino={(destino) => this.filtrarBolsines({ filtroDestino: destino })}
                  onLimpiar={() => this.filtrarBolsines(FILTROS_VACIOS)}
                />
              )}

              {/* A2: no se encuentra bolsín con el número de precinto ingresado */}
              {hayBolsines && visibles.length === 0 && (
                <div className="alerta alerta-aviso" role="alert">
                  <strong>
                    {filtroPrecinto.trim()
                      ? `No se encontró ningún bolsín con el precinto «${filtroPrecinto.trim()}».`
                      : 'Ningún bolsín coincide con el filtro.'}
                  </strong>
                  <p>Revisá el número de precinto o limpiá el filtro para ver todos los bolsines.</p>
                </div>
              )}

              {visibles.length > 0 && (
                <div className="contenido">
                  <TablaBolsines
                    bolsines={visibles}
                    seleccionHabilitada={seleccionHabilitada}
                    numeroElegido={numeroElegido}
                    onElegir={(numeroBolsin) => this.elegirBolsin(numeroBolsin)}
                  />
                  <MapaBolsines
                    mapa={mapaVisible}
                    numeroElegido={numeroResaltado}
                    seleccionHabilitada={seleccionHabilitada}
                    onElegir={(numeroBolsin) => this.elegirBolsin(numeroBolsin)}
                  />
                </div>
              )}

              {/* Después de solicitarSelBolsinTipo(): barra para elegir, continuar o cancelar */}
              {(seleccionHabilitada || finCasoDeUso) && (
                <SeleccionBolsin
                  bolsines={visibles}
                  numeroElegido={numeroElegido}
                  errorNotificacion={errorNotificacion}
                  finCasoDeUso={finCasoDeUso}
                  onContinuar={() => this.tomarSeleccionBolsin()}
                  onCancelar={() => this.cancelarCasoDeUso()}
                  onVolverAlMenu={() => this.props.onVolverAlMenu()}
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
            onEnviar={() => this.tomarConfirmacion(true)}
            onNoEnviar={() => this.tomarConfirmacion(false)}
            onVolver={() => this.cancelarEnvio()}
          />
        )}
      </div>
    )
  }
}
