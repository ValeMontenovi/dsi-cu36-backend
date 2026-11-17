// =====================================================================
// ENTIDAD: Usuario
// ---------------------------------------------------------------------
// Persona que puede ingresar al sistema con un perfil (ERS, actor
// "Usuario"). La usa el CU 5 "Iniciar sesión".
//
//   mail          → con el que inicia sesión
//   contrasena    → la que valida el inicio de sesión
//   nombre        → cómo se lo saluda en el menú
//   perfil        → actor de la ERS (ej. "Encargado de Bolsines")
//   nombreUsuario → el usuario que conoce el backend (ej. "encargado-cba")
//   comisionMedica→ CM a la que está asignado { codigo, nombre }
// =====================================================================

export default class Usuario {
  constructor({ mail, contrasena, nombre, perfil, nombreUsuario, comisionMedica }) {
    this.mail = mail
    this.contrasena = contrasena
    this.nombre = nombre
    this.perfil = perfil
    this.nombreUsuario = nombreUsuario
    this.comisionMedica = comisionMedica
  }

  // ¿Este usuario tiene el mail que se ingresó? (no distingue mayúsculas)
  esTuMail(mail) {
    return this.mail.toLowerCase() === mail.trim().toLowerCase()
  }

  // ¿La contraseña ingresada es la de este usuario?
  validarContrasena(contrasena) {
    return this.contrasena === contrasena
  }

  getNombre() {
    return this.nombre
  }

  getPerfil() {
    return this.perfil
  }

  getNombreUsuario() {
    return this.nombreUsuario
  }

  getComisionMedica() {
    return this.comisionMedica
  }

  getMail() {
    return this.mail
  }
}
