import { Component } from 'react'
import GestorInicioSesion from '../control/GestorInicioSesion'
import PantallaInicioSesion from './PantallaInicioSesion'
import PantallaMenuPrincipal from './PantallaMenuPrincipal'
import PantallaSeguimientoBolsin from './PantallaSeguimientoBolsin'
import { CU_CONSULTAR_SEGUIMIENTO } from '../servicios/opcionesDelSistema'

// =====================================================================
// Aplicacion: decide qué pantalla se ve
// ---------------------------------------------------------------------
//   sin sesión          → PantallaInicioSesion       (CU 5)
//   con sesión          → PantallaMenuPrincipal      (opciones del perfil)
//   eligió la opción 36 → PantallaSeguimientoBolsin  (CU 36)
// Cerrar sesión (CU 6) vuelve a la pantalla de inicio desde cualquier lado.
// =====================================================================

export default class Aplicacion extends Component {
  constructor(props) {
    super(props)
    this.gestorInicioSesion = new GestorInicioSesion()
    // Si la pestaña ya tenía una sesión abierta (por ejemplo, al recargar), se recupera
    this.state = { sesion: this.gestorInicioSesion.recuperarSesion(), opcionElegida: null }
  }

  iniciarSesion(sesion) {
    this.setState({ sesion, opcionElegida: null })
  }

  cerrarSesion() {
    this.gestorInicioSesion.cerrarSesion()
    this.setState({ sesion: null, opcionElegida: null })
  }

  render() {
    const { sesion, opcionElegida } = this.state

    if (!sesion) {
      return <PantallaInicioSesion gestor={this.gestorInicioSesion} onSesionIniciada={(nueva) => this.iniciarSesion(nueva)} />
    }

    if (opcionElegida === CU_CONSULTAR_SEGUIMIENTO) {
      return (
        <PantallaSeguimientoBolsin
          sesion={sesion}
          onVolverAlMenu={() => this.setState({ opcionElegida: null })}
          onCerrarSesion={() => this.cerrarSesion()}
        />
      )
    }

    return (
      <PantallaMenuPrincipal
        sesion={sesion}
        opciones={this.gestorInicioSesion.buscarOpcionesDelUsuario(sesion)}
        onElegirOpcion={(numero) => this.setState({ opcionElegida: numero })}
        onCerrarSesion={() => this.cerrarSesion()}
      />
    )
  }
}
