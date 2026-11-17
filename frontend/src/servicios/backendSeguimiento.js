// =====================================================================
// SERVICIO: comunicación con el backend (Java + Spring Boot, puerto 8081)
// ---------------------------------------------------------------------
// Es el ÚNICO archivo del frontend que hace pedidos HTTP.
// La ruta empieza con /api, por eso Vite la reenvía al backend
// (ver "proxy" en vite.config.js).
// =====================================================================

/**
 * Pide al backend el seguimiento de los bolsines enviados por la CM del usuario.
 *
 *   GET /api/seguimiento?usuario=encargado-cba
 *
 * En el backend lo atiende GestorSeguimientoBolsin.consultar(), que hace:
 *   buscarCMUsuario() → buscarBolsinesEnviadoCM() → localizarBolsines()
 *
 * y localizarBolsines() es donde se aplica el PATRÓN ADAPTER:
 *   InterfazGPSTracker.obtenerLocalizacion()
 *     → AdaptadorXTR4500L / AdaptadorNavTrackQX7A / AdaptadorGeoPulseMTR900
 *       .recibirDatosLocalizacion()
 *         → solicitarLocalizacion() → getBolsinLocation() (GPS Tracker)
 *         → adaptarDatosRelevados()
 *
 * Devuelve un array con un objeto por bolsín, por ejemplo:
 *   {
 *     numeroBolsin: 101, numeroPrecinto: "P-101",
 *     cmOrigen: "Córdoba", cmDestino: "Rosario", modeloGps: "XTR_4500L",
 *     ubicacion: { latitud: -31.42, longitud: -64.18, fechaHoraActualizacion: "2026-11-15T11:00:00Z" },
 *     errorGps: null
 *   }
 * Si el GPS de un bolsín no respondió: ubicacion = null y errorGps trae el motivo.
 */
export async function pedirSeguimientoBolsines(usuario) {
  const parametros = new URLSearchParams({ usuario })
  const respuesta = await fetch(`/api/seguimiento?${parametros}`)

  if (!respuesta.ok) {
    // El backend manda el motivo del error como texto (ej. "Usuario no encontrado: ...").
    // Si no manda nada, casi siempre es porque el backend está apagado.
    const motivo = await respuesta.text()
    throw new Error(
      motivo || `No se pudo obtener respuesta del backend (error ${respuesta.status}). ¿Está levantado en el puerto 8081?`,
    )
  }

  return respuesta.json()
}

/**
 * Pide al backend que notifique por mail la ubicación del bolsín seleccionado.
 *
 *   POST /api/seguimiento/101/notificar?usuario=encargado-cba
 *
 * Lo usa GestorSeguimientoBolsin.regConfirmacion(). En el backend se hace el resto
 * del diagrama: buscarMail() → Bucle [Mientras haya Empleados] → llamarCU31/NotificarUbicacionBolsin().
 *
 * El envío solo se da por hecho con un 204 real (ver backend/CONTRATO_FRONTEND.md).
 * Hoy el backend responde 501 porque falta configurar el proveedor de correo:
 * en ese caso se informa el error, nunca se muestra como enviado.
 */
export async function pedirNotificacionUbicacion(usuario, numeroBolsin) {
  const parametros = new URLSearchParams({ usuario })
  const respuesta = await fetch(`/api/seguimiento/${numeroBolsin}/notificar?${parametros}`, { method: 'POST' })

  if (respuesta.status === 204) return

  const motivo = await respuesta.text()
  if (respuesta.status === 501) {
    throw new Error(`El backend respondió 501 (el envío de mails todavía no está configurado). ${motivo}`.trim())
  }
  throw new Error(
    motivo || `No se pudo obtener respuesta del backend (error ${respuesta.status}). ¿Está levantado en el puerto 8081?`,
  )
}
