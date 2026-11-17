// =====================================================================
// ENTIDAD: Sesion   (en el diagrama de secuencia: "Activa:Sesion")
// ---------------------------------------------------------------------
// Se crea cuando el usuario inicia sesión con su mail y contraseña
// (CU 5 "Iniciar sesión") y se descarta al cerrar sesión (CU 6).
// Guarda quién está usando el sistema y desde cuándo.
// =====================================================================

export default class Sesion {
  constructor(usuario, fechaHoraInicio = new Date()) {
    this.usuario = usuario // Usuario que inició sesión
    this.fechaHoraInicio = fechaHoraInicio
  }

  // Diagrama: GestorSeguimientoBolsin → Activa:Sesion : getUsuario()
  // Devuelve el usuario como lo conoce el backend (ej. "encargado-cba"),
  // que es con el que el backend busca la CM del usuario logueado.
  getUsuario() {
    return this.usuario.getNombreUsuario()
  }

  // Los datos completos del usuario logueado (nombre, perfil, CM), para mostrarlos en pantalla.
  getUsuarioLogueado() {
    return this.usuario
  }

  getFechaHoraInicio() {
    return this.fechaHoraInicio
  }
}
