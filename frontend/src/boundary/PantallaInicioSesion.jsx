import { Component } from 'react'
import { IconoBolsin } from '../vistas/Iconos'
import DibujoRedComisiones from '../vistas/DibujoRedComisiones'

// =====================================================================
// BOUNDARY: PantallaInicioSesion   (CU 5 "Iniciar sesión" de la ERS)
// ---------------------------------------------------------------------
// Pide el mail y la contraseña y se los pasa al GestorInicioSesion.
// Si son correctos, avisa a la Aplicacion con onSesionIniciada(sesion)
// para que muestre el menú principal.
//
// Estado (this.state):
//   mail, contrasena    → lo que escribe el usuario
//   mostrarContrasena   → el ojito para ver la contraseña
//   error               → motivo por el que no pudo entrar (flujo alternativo)
// =====================================================================

export default class PantallaInicioSesion extends Component {
  constructor(props) {
    super(props)
    this.state = { mail: '', contrasena: '', mostrarContrasena: false, error: null }
  }

  // Se ejecuta con el botón "Iniciar sesión" (o Enter en el formulario)
  tomarDatosIngreso(evento) {
    evento.preventDefault()
    try {
      const sesion = this.props.gestor.iniciarSesion(this.state.mail, this.state.contrasena)
      this.props.onSesionIniciada(sesion)
    } catch (error) {
      // Flujo alternativo: datos vacíos o incorrectos. Se borra la contraseña.
      this.setState({ error: error.message, contrasena: '' })
    }
  }

  render() {
    const { mail, contrasena, mostrarContrasena, error } = this.state

    return (
      <div className="inicio">
        {/* Lado azul: nombre del sistema y el dibujo de la red de comisiones médicas */}
        <section className="inicio-presentacion">
          <div className="marca">
            <IconoBolsin />
            <div>
              <h1>Bolsines</h1>
              <p>Organismo controlador</p>
            </div>
          </div>

          <DibujoRedComisiones />

          <div className="inicio-lema">
            <h2>Cada bolsín, de una comisión médica a otra.</h2>
            <p>Registrá el envío y la recepción de la documentación, y seguí por GPS los bolsines que están en viaje.</p>
          </div>
        </section>

        {/* Lado blanco: el formulario */}
        <main className="inicio-formulario">
          <form className="formulario" onSubmit={(evento) => this.tomarDatosIngreso(evento)} noValidate>
            <h2 className="formulario-titulo">Iniciar sesión</h2>
            <p className="formulario-texto">Entrá con el mail y la contraseña que te asignó tu comisión médica.</p>

            <label className="campo">
              <span>Mail</span>
              <input
                type="email"
                name="mail"
                autoComplete="username"
                placeholder="nombre@bolsines.test"
                value={mail}
                onChange={(evento) => this.setState({ mail: evento.target.value, error: null })}
                aria-invalid={Boolean(error)}
                autoFocus
              />
            </label>

            <label className="campo">
              <span>Contraseña</span>
              <span className="campo-con-boton">
                <input
                  type={mostrarContrasena ? 'text' : 'password'}
                  name="contrasena"
                  autoComplete="current-password"
                  value={contrasena}
                  onChange={(evento) => this.setState({ contrasena: evento.target.value, error: null })}
                  aria-invalid={Boolean(error)}
                />
                <button
                  type="button"
                  className="boton-ojo"
                  onClick={() => this.setState({ mostrarContrasena: !mostrarContrasena })}
                  aria-label={mostrarContrasena ? 'Ocultar la contraseña' : 'Mostrar la contraseña'}
                  aria-pressed={mostrarContrasena}
                >
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <g fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round">
                      <path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z" />
                      <circle cx="12" cy="12" r="3" />
                      {mostrarContrasena && <path d="M4 20L20 4" />}
                    </g>
                  </svg>
                </button>
              </span>
            </label>

            {error && (
              <p className="formulario-error" role="alert">
                {error}
              </p>
            )}

            <button type="submit" className="boton-principal boton-ancho">
              Iniciar sesión
            </button>

            <p className="formulario-ayuda">
              Usuarios de prueba: ver <code>frontend/README.md</code>.
            </p>
          </form>
        </main>
      </div>
    )
  }
}
