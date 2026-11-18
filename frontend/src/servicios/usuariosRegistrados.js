import Usuario from '../entidad/Usuario'

// =====================================================================
// SERVICIO: usuariosRegistrados   (datos de prueba)
// ---------------------------------------------------------------------
// Usuarios con los que se puede iniciar sesión.
//
// IMPORTANTE: el backend todavía no tiene inicio de sesión (no guarda
// mails ni contraseñas), así que los usuarios se validan acá, en el
// navegador. Sirve para el PPAI, NO es seguro para un sistema real:
// la contraseña tiene que validarla el backend.
//
// "nombreUsuario" coincide con los empleados que carga el backend en
// el perfil demo (DatosDemo.java): encargado-cba, encargado-ros,
// encargado-mdz, encargado-ba y encargado-pna. Así el CU 36 trae los
// bolsines de la CM correcta.
//
// Todos usan la contraseña de prueba: Bolsines2026
// =====================================================================

const CONTRASENA_DE_PRUEBA = 'Bolsines2026'

export const USUARIOS_REGISTRADOS = [
  new Usuario({
    mail: 'encargado.cordoba@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Lucía Ferreyra',
    perfil: 'Encargado de Bolsines',
    nombreUsuario: 'encargado-cba',
    comisionMedica: { codigo: 'CM-CBA-01', nombre: 'Córdoba' },
  }),
  new Usuario({
    mail: 'encargado.rosario@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Martín Acosta',
    perfil: 'Encargado de Bolsines',
    nombreUsuario: 'encargado-ros',
    comisionMedica: { codigo: 'CM-ROS-02', nombre: 'Rosario' },
  }),
  new Usuario({
    mail: 'encargado.mendoza@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Sofía Quiroga',
    perfil: 'Encargado de Bolsines',
    nombreUsuario: 'encargado-mdz',
    comisionMedica: { codigo: 'CM-MDZ-03', nombre: 'Mendoza' },
  }),
  // Buenos Aires: tiene un bolsín que el GPS no puede ubicar (flujo alternativo A3)
  new Usuario({
    mail: 'encargado.buenosaires@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Carolina Benítez',
    perfil: 'Encargado de Bolsines',
    nombreUsuario: 'encargado-ba',
    comisionMedica: { codigo: 'CM-BA-01', nombre: 'Buenos Aires' },
  }),
  // Paraná: no tiene bolsines enviados (flujo alternativo A1)
  new Usuario({
    mail: 'encargado.parana@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Diego Sosa',
    perfil: 'Encargado de Bolsines',
    nombreUsuario: 'encargado-pna',
    comisionMedica: { codigo: 'CM-PNA-01', nombre: 'Paraná' },
  }),
  // Otro perfil, para mostrar que cada actor ve sus propias opciones.
  // No existe en el backend: no puede usar el CU 36.
  new Usuario({
    mail: 'gerente.cordoba@bolsines.test',
    contrasena: CONTRASENA_DE_PRUEBA,
    nombre: 'Javier Molina',
    perfil: 'Gerente de Comisión Médica',
    nombreUsuario: 'gerente-cba',
    comisionMedica: { codigo: 'CM-CBA-01', nombre: 'Córdoba' },
  }),
]
