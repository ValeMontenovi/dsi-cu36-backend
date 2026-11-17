# Contrato GPS del CU 36

## Firmas

Boundary concreta `InterfazGPSTracker`:

```java
String[][] obtenerLocalizacion(
  int[] nroBolsines,
  String[] modelosGps,
  String codigoCMOrigen,
  String[] codigosCMDestino
);
```

Contrato comun `IAdaptadorGPSTracker`, implementado por `AdaptadorXTR4500L`,
`AdaptadorNavTrackQX7A` y `AdaptadorGeoPulseMTR900` directamente, sin una superclase de adaptadores:

```java
String[][] recibirDatosLocalizacion(
  int[] nroBolsines,
  String codigoCMOrigen,
  String[] codigosCMDestino
);
```

## Responsabilidades y orden

El gestor selecciona los bolsines enviados de la CM del empleado y aplica los filtros existentes.
Construye los tres arreglos paralelos con numero, nombre del modelo y codigo de destino de cada bolsin.
Convierte `Long` a `int` mediante `Math.toIntExact`; fuera de rango informa un argumento invalido.
No selecciona adaptadores ni interpreta JSON o CSV. Solo convierte el contrato normalizado al DTO
REST existente; los endpoints y sus campos JSON conservan su estructura.

La boundary valida arreglos no nulos, igual longitud y modelos reconocidos antes de consultar.
Los nombres admitidos son `XTR_4500L`, `NAVTRACK_QX_7A`, `GEOPULSE_MTR_900` (nombres de `ModeloGps`).
Un contrato estructural invalido provoca `IllegalArgumentException` (HTTP 400 en el controller).
La boundary no consulta repositorios. Usa un mapa local a la invocacion para crear como maximo
un adaptador de cada modelo presente y reutilizarlo mientras recorre las posiciones.
Cada delegacion recibe un numero y su destino en arreglos de un elemento.
No agrupa ni reordena resultados: incluso numeros repetidos mantienen su propia posicion.
Los adaptadores tambien aceptan lotes y validan igual longitud de numeros y destinos.

## Matriz normalizada

Cada entrada genera exactamente una fila con cinco columnas:

| Indice | Contenido              | Exito                        | Fallo                        |
| ------ | ---------------------- | ---------------------------- | ---------------------------- |
| 0      | numeroBolsin           | Numero solicitado en decimal | Numero solicitado en decimal |
| 1      | latitud                | Decimal con punto            | `null`                       |
| 2      | longitud               | Decimal con punto            | `null`                       |
| 3      | fechaHoraActualizacion | ISO 8601 con desplazamiento  | `null`                       |
| 4      | errorGps               | `null`                       | Mensaje descriptivo no vacio |

No hay encabezado. La matriz tiene tantas filas como entradas, en su mismo orden.
Una entrada vacia produce cero filas. Los valores ausentes son referencias Java `null`,
no cadenas `"null"`, vacias ni coordenadas cero. `(0, 0)` sigue siendo una ubicacion real valida.
No se acepta una respuesta de otro bolsin: se compara el numero recibido con el solicitado
antes de escribir las columnas de exito. Se rechazan tipos incorrectos, campos faltantes,
fechas invalidas, coordenadas no finitas y latitudes/longitudes fuera de rango.
Las respuestas a consultas individuales deben tener exactamente un registro.

Un fallo HTTP, de interpretacion, de numero o de datos requeridos afecta solo esa fila;
las consultas posteriores continuan. XTR exige origen y NavTrack exige destino.
El gestor representa esas filas como `ubicacion: null` y conserva `errorGps` en su respuesta REST.

## Formatos externos

Referencia: [README del simulador de la catedra](https://github.com/LiberaeMentis/dsi-api-bolsines/blob/main/README.md).
Se mantiene `X-API-Key` y la ruta configurable de cada proveedor.

- XTR: numero y CM origen; JSON `{"items":[{"numeroBolsin":101,"latitud":-31.42,"longitud":-64.18,"fechaHoraUltimaActualizacion":"2026-03-07T12:30:00"}]}`.
- NavTrack: numero y CM destino; texto CSV `101,-31.42,-64.18,2026-03-07T12:30`.
- GeoPulse: numero; matriz JSON `[[101,-31.42,-64.18,"2026-03-07T12:30:00"]]`.

Las fechas externas sin zona conservan la convencion UTC de la implementacion anterior;
no se infiere la zona por coordenadas. Las fechas con desplazamiento lo conservan.
Esta convencion es del backend: los ejemplos sin zona del simulador no establecen su huso.

No se implementa mapa ni proveedor de correo. El consumidor existente de notificacion
solo se adapta para obtener su ubicacion mediante esta boundary y rechaza filas fallidas.
