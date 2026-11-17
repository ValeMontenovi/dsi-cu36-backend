import { IconoBolsin } from './Iconos'

// =====================================================================
// VISTA: BarraSuperior
// ---------------------------------------------------------------------
// Barra azul de arriba que comparten el menú principal y el CU 36:
// nombre del sistema, usuario logueado y los botones que se le pasen
// (volver al menú, cerrar sesión). Solo dibuja.
// =====================================================================

// "Lucía Ferreyra" → "LF"
function iniciales(nombre) {
  return nombre
    .split(' ')
    .map((parte) => parte[0])
    .slice(0, 2)
    .join('')
}

export default function BarraSuperior({ titulo, subtitulo, sesion, children }) {
  const usuario = sesion.getUsuarioLogueado()

  return (
    <header className="barra">
      <div className="barra-interior">
        <div className="marca">
          <IconoBolsin />
          <div>
            <h1>{titulo}</h1>
            <p>{subtitulo}</p>
          </div>
        </div>

        <div className="barra-derecha">
          <div className="usuario-logueado">
            <span className="avatar" aria-hidden="true">
              {iniciales(usuario.getNombre())}
            </span>
            <span>
              <strong>{usuario.getNombre()}</strong>
              <small>
                {usuario.getPerfil()}, CM {usuario.getComisionMedica().nombre}
              </small>
            </span>
          </div>
          {children}
        </div>
      </div>
    </header>
  )
}
