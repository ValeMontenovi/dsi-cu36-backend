import { Component } from 'react'
import BarraSuperior from '../vistas/BarraSuperior'
import IconoOpcion from '../vistas/IconosMenu'
import CicloDeVidaBolsin from '../vistas/CicloDeVidaBolsin'
import { CU_CERRAR_SESION, CU_CONSULTAR_SEGUIMIENTO, GRUPOS } from '../servicios/opcionesDelSistema'

// =====================================================================
// BOUNDARY: PantallaMenuPrincipal
// ---------------------------------------------------------------------
// Después de iniciar sesión muestra todas las opciones (casos de uso de
// la ERS) que tiene el perfil del usuario logueado.
// Solo dos funcionan:
//   • CU 36 "Consultar seguimiento de bolsines" → onElegirOpcion(36)
//   • CU 6  "Cerrar sesión"                      → onCerrarSesion()
// Las demás se ven, pero quedan deshabilitadas con "Próximamente".
// =====================================================================

// Los pasos del CU 36 que se dibujan en la tarjeta destacada
const PASOS_SEGUIMIENTO = [
  'Busca los bolsines enviados por tu CM',
  'Pide la ubicación a cada GPS Tracker',
  'Los muestra en el mapa',
  'Avisa por mail al gerente de la CM destino',
]

function saludo(hora) {
  if (hora < 12) return 'Buen día'
  if (hora < 20) return 'Buenas tardes'
  return 'Buenas noches'
}

export default class PantallaMenuPrincipal extends Component {
  // El actor elige una opción del menú
  tomarSeleccionOpcion(opcion) {
    if (opcion.numero === CU_CERRAR_SESION) this.props.onCerrarSesion()
    else if (opcion.operativa) this.props.onElegirOpcion(opcion.numero)
  }

  render() {
    const { sesion, opciones } = this.props
    const usuario = sesion.getUsuarioLogueado()
    const primerNombre = usuario.getNombre().split(' ')[0]
    const seguimiento = opciones.find((opcion) => opcion.numero === CU_CONSULTAR_SEGUIMIENTO)
    const grupos = GRUPOS.map((grupo) => ({
      grupo,
      opciones: opciones.filter((opcion) => opcion.grupo === grupo),
    })).filter((grupo) => grupo.opciones.length > 0)

    return (
      <div className="app">
        <BarraSuperior titulo="Bolsines" subtitulo="Menú principal" sesion={sesion}>
          <button className="boton-barra" onClick={() => this.props.onCerrarSesion()}>
            Cerrar sesión
          </button>
        </BarraSuperior>

        <main className="pantalla menu-principal">
          <header className="menu-saludo">
            <h2>
              {saludo(new Date().getHours())}, {primerNombre}
            </h2>
            <p>
              {usuario.getPerfil()} de la Comisión Médica {usuario.getComisionMedica().nombre}. Iniciaste sesión a las{' '}
              {sesion.getFechaHoraInicio().toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit', hour12: false })}.
            </p>
          </header>

          {/* Tarjeta destacada: el caso de uso que ya funciona */}
          {seguimiento && (
            <section className="opcion-destacada" aria-labelledby="titulo-seguimiento">
              <div className="destacada-texto">
                <h3 id="titulo-seguimiento">{seguimiento.nombre}</h3>
                <p>Mirá en un mapa dónde está cada bolsín que salió de tu comisión médica.</p>
                <button className="boton-principal" onClick={() => this.tomarSeleccionOpcion(seguimiento)}>
                  <IconoOpcion nombre="mapa" />
                  Registrar seguimiento de bolsines
                </button>
              </div>

              {/* Gráfico: los pasos del caso de uso sobre una ruta */}
              <ol className="destacada-pasos">
                {PASOS_SEGUIMIENTO.map((paso) => (
                  <li key={paso}>{paso}</li>
                ))}
              </ol>
            </section>
          )}

          {/* Todas las opciones del perfil, agrupadas */}
          <div className="menu-grupos">
            {grupos.map(({ grupo, opciones: opcionesDelGrupo }) => (
              <section key={grupo} className="grupo" aria-labelledby={`grupo-${grupo}`}>
                <h2 id={`grupo-${grupo}`} className="titulo-seccion">
                  {grupo}
                </h2>
                <ul className="opciones">
                  {opcionesDelGrupo.map((opcion) => (
                    <li key={opcion.numero}>
                      <button
                        className={opcion.operativa ? 'opcion' : 'opcion opcion-pendiente'}
                        onClick={() => this.tomarSeleccionOpcion(opcion)}
                        disabled={!opcion.operativa}
                      >
                        <IconoOpcion nombre={opcion.icono} />
                        <span className="opcion-nombre">{opcion.nombre}</span>
                        {!opcion.operativa && <span className="opcion-estado">Próximamente</span>}
                      </button>
                    </li>
                  ))}
                </ul>
              </section>
            ))}
          </div>

          {/* Gráfico chico de estados: solo para quien maneja bolsines */}
          {seguimiento && <CicloDeVidaBolsin />}
        </main>
      </div>
    )
  }
}
