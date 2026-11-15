# Contrato provisional entre backend y frontend — CU 36

**Acordar con el compañero de frontend antes de integrar.** Los datos mostrados son de ejemplo y la API GPS externa todavía requiere validación.

## Consultar seguimiento

`GET http://localhost:8081/api/seguimiento?usuario=encargado-cba`

Filtros optativos: `precinto=P-101` y `destino=ROS`. `usuario` representa una simulación de sesión para el TP; no usar como autenticación real.

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

## Notificar ubicación

`POST http://localhost:8081/api/seguimiento/101/notificar?usuario=encargado-cba`

En éxito devolverá `204 No Content`, pero **hoy devuelve `501 Not Implemented` porque no se configuró el proveedor de correo**. No presentar esto como implementado.

## Datos que el frontend NO debe inventar

- Posición GPS y hora de actualización: deben venir de la API simulada a través del backend
- CM del usuario: debe provenir del sistema, no de un valor codificado en la pantalla
- Envío confirmado: solamente después de un `204` real
