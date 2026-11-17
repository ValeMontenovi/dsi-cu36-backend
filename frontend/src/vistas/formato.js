// Funciones de formato que usan las vistas.

// Convierte una fecha del backend ("2026-11-15T11:00:00Z", en hora UTC)
// a la hora argentina: "15/11/2026, 08:00:00".
export function formatearFechaHora(fechaIso) {
  return new Date(fechaIso).toLocaleString('es-AR')
}

// Muestra una coordenada con 4 decimales (alcanza para ubicar un vehículo).
export function formatearCoordenada(numero) {
  return numero.toFixed(4)
}
