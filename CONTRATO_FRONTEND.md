# Contrato provisional entre backend y frontend — CU 36

**Acordar con el compañero de frontend antes de integrar.** Los datos mostrados son de ejemplo. La integración HTTP del backend con los tres modelos del simulador GPS ya fue verificada; la integración del frontend está pendiente.

## Consultar seguimiento

`GET http://localhost:8081/api/seguimiento?usuario=encargado-cba`

Filtros optativos: `precinto=P-101` y `destino=CM-ROS-02`. El destino se compara por código exacto. El controller crea una sesión demo por petición a partir de `usuario`; no usar como autenticación real.

Respuesta 200 (array):

```json
[
  {
    "numeroBolsin": 101,
    "numeroPrecinto": "P-101",
    "cmOrigen": "Córdoba",
    "cmDestino": "Rosario",
    "modeloGps": "XTR_4500L",
    "ubicacion": {
      "numeroBolsin": 101,
      "latitud": -31.42,
      "longitud": -64.18,
      "fechaHoraActualizacion": "2026-11-15T11:00:00Z"
    },
    "errorGps": null
  }
]
```

Si el GPS no responde, el bolsín permanece en la lista con `ubicacion: null` y `errorGps` descriptivo. `[]` significa que no hay bolsines enviados o que no coinciden con el filtro.

El historial determina el estado vigente. Si un bolsín de la CM consultada tiene cero o varios cambios actuales, se devuelve `409 Conflict` con un mensaje explícito de inconsistencia. Las fechas GPS sin zona se interpretan como UTC por convención del backend.

## Notificar ubicación

`POST http://localhost:8081/api/seguimiento/101/notificar?usuario=encargado-cba`

En éxito devuelve `204 No Content`: el mail con el número de bolsín, la fecha y hora de la última actualización y las coordenadas se envió al gerente de la CM destino. Sin servidor de correo configurado, el backend lo guarda como archivo `.eml` en `backend/data/mails/` (ver `backend/INSTALACION_WINDOWS.md`).

Errores: `400` si el bolsín no es del usuario o no está enviado, `503` si el GPS no informa su ubicación y `502 Bad Gateway` si el servidor de correo rechaza el envío. En esos casos el frontend muestra el motivo y nunca lo presenta como enviado.

No enviar el mail (flujo alternativo A5) no llama a este endpoint.

## Datos que el frontend NO debe inventar

- Posición GPS y hora de actualización: deben venir de la API simulada a través del backend
- CM del usuario: debe provenir del sistema, no de un valor codificado en la pantalla
- Envío confirmado: solamente después de un `204` real
