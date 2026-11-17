// =====================================================================
// SERVICIO: opcionesDelSistema
// ---------------------------------------------------------------------
// Las opciones del menú principal, sacadas del "Listado de la
// Funcionalidad del Sistema" de la ERS (punto 3.3).
//
// Cada opción es un caso de uso:
//   numero     → número del CU en la ERS
//   nombre     → nombre del CU en la ERS
//   grupo      → sección del menú donde aparece
//   icono      → dibujo que la acompaña (ver vistas/IconosMenu.jsx)
//   perfiles   → actores que la pueden usar (los de la columna "Actores")
//   operativa  → true solo si ya está programada
//
// Las opciones del actor "Usuario" (2, 4, 5, 6) las heredan todos los
// perfiles, porque en la ERS "Usuario" es el actor abstracto.
// =====================================================================

export const ENCARGADO_DE_BOLSINES = 'Encargado de Bolsines'
export const GERENTE_DE_CM = 'Gerente de Comisión Médica'
const TODOS = [ENCARGADO_DE_BOLSINES, GERENTE_DE_CM]

// Opción que dispara el CU 36 (la única operativa además de cerrar sesión)
export const CU_CONSULTAR_SEGUIMIENTO = 36
export const CU_CERRAR_SESION = 6

export const GRUPOS = ['Bolsines', 'Envío y recepción', 'Remitos', 'Reportes', 'Mi cuenta']

export const OPCIONES_DEL_SISTEMA = [
  { numero: 19, nombre: 'Generar bolsín', grupo: 'Bolsines', icono: 'generar', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 20, nombre: 'Modificar bolsín', grupo: 'Bolsines', icono: 'modificar', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 21, nombre: 'Eliminar bolsín', grupo: 'Bolsines', icono: 'eliminar', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 22, nombre: 'Consultar bolsín', grupo: 'Bolsines', icono: 'consultar', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 39, nombre: 'Cerrar bolsín', grupo: 'Bolsines', icono: 'precinto', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 40, nombre: 'Reabrir bolsín', grupo: 'Bolsines', icono: 'reabrir', perfiles: [ENCARGADO_DE_BOLSINES] },

  { numero: 27, nombre: 'Registrar el retiro de bolsines', grupo: 'Envío y recepción', icono: 'camion', perfiles: [ENCARGADO_DE_BOLSINES] },
  { numero: 28, nombre: 'Registrar recepción de bolsín', grupo: 'Envío y recepción', icono: 'recepcion', perfiles: [ENCARGADO_DE_BOLSINES] },

  { numero: 17, nombre: 'Cancelar remito', grupo: 'Remitos', icono: 'remito', perfiles: [ENCARGADO_DE_BOLSINES] },

  { numero: 37, nombre: 'Generar reporte de bolsines en tránsito', grupo: 'Reportes', icono: 'reporte', perfiles: [GERENTE_DE_CM] },
  { numero: 38, nombre: 'Generar estadística de movimiento de bolsines', grupo: 'Reportes', icono: 'estadistica', perfiles: [GERENTE_DE_CM] },

  { numero: 4, nombre: 'Consultar usuario', grupo: 'Mi cuenta', icono: 'usuario', perfiles: TODOS },
  { numero: 2, nombre: 'Modificar usuario', grupo: 'Mi cuenta', icono: 'modificar', perfiles: TODOS },
  { numero: CU_CERRAR_SESION, nombre: 'Cerrar sesión', grupo: 'Mi cuenta', icono: 'salir', perfiles: TODOS, operativa: true },

  // El CU 36 va aparte, destacado arriba del menú
  {
    numero: CU_CONSULTAR_SEGUIMIENTO,
    nombre: 'Consultar seguimiento de bolsines',
    grupo: 'Seguimiento',
    icono: 'mapa',
    perfiles: [ENCARGADO_DE_BOLSINES],
    operativa: true,
  },
]
