import Sesion from '../entidad/Sesion'
import { USUARIOS_REGISTRADOS } from '../servicios/usuariosRegistrados'
import { OPCIONES_DEL_SISTEMA } from '../servicios/opcionesDelSistema'

// =====================================================================
// CONTROL: GestorInicioSesion
// ---------------------------------------------------------------------
// Resuelve el CU 5 "Iniciar sesión" y el CU 6 "Cerrar sesión" (ERS):
//   1. busca el usuario por su mail,
//   2. valida su contraseña,
//   3. crea la Sesion activa,
//   4. busca las opciones del menú que le corresponden a su perfil.
//
// La sesión se recuerda mientras la pestaña esté abierta
// (sessionStorage), así recargar la página no te saca del sistema.
// =====================================================================

const CLAVE_GUARDADO = 'bolsines.sesion'

export default class GestorInicioSesion {
  // Devuelve la Sesion creada o lanza un Error con el motivo (flujo alternativo).
  iniciarSesion(mail, contrasena) {
    if (!mail.trim() || !contrasena) {
      throw new Error('Ingresá tu mail y tu contraseña.')
    }

    const usuario = this.buscarUsuario(mail)
    // Mismo mensaje en los dos casos: no se le dice a un extraño qué mails existen
    if (!usuario || !usuario.validarContrasena(contrasena)) {
      throw new Error('El mail o la contraseña no son correctos.')
    }

    const sesion = new Sesion(usuario)
    this.guardarSesion(sesion)
    return sesion
  }

  // Busca entre los usuarios registrados el que tiene ese mail
  buscarUsuario(mail) {
    return USUARIOS_REGISTRADOS.find((usuario) => usuario.esTuMail(mail)) ?? null
  }

  // Opciones del menú que puede usar el perfil del usuario logueado
  buscarOpcionesDelUsuario(sesion) {
    const perfil = sesion.getUsuarioLogueado().getPerfil()
    return OPCIONES_DEL_SISTEMA.filter((opcion) => opcion.perfiles.includes(perfil))
  }

  // CU 6 "Cerrar sesión"
  cerrarSesion() {
    try {
      sessionStorage.removeItem(CLAVE_GUARDADO)
    } catch {
      // Si el navegador no deja guardar, no hay nada que borrar
    }
  }

  // Al recargar la página: si había una sesión abierta en esta pestaña, se recupera
  recuperarSesion() {
    try {
      const guardada = JSON.parse(sessionStorage.getItem(CLAVE_GUARDADO))
      const usuario = guardada ? this.buscarUsuario(guardada.mail) : null
      return usuario ? new Sesion(usuario, new Date(guardada.fechaHoraInicio)) : null
    } catch {
      return null
    }
  }

  guardarSesion(sesion) {
    try {
      const datos = { mail: sesion.getUsuarioLogueado().getMail(), fechaHoraInicio: sesion.getFechaHoraInicio() }
      sessionStorage.setItem(CLAVE_GUARDADO, JSON.stringify(datos))
    } catch {
      // Sin almacenamiento la sesión dura hasta recargar la página
    }
  }
}
