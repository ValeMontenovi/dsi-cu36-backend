# DSI Bolsines — CU 36 | Backend Java Spring Boot

Backend académico del CU 36 **Consultar seguimiento de bolsines**, con patrón GoF **Adapter**.

## Estado

- Sesión demo inmutable por petición: el controller crea `Sesion` desde el parámetro `usuario`; no hay autenticación real ni usuario almacenado en el gestor compartido.
- Identificación de la CM del empleado: `Sesion.getUsuario()` → recorrido de empleados con `esTuUsuario()` → `Empleado.obtenerCM()` → `ComisionMedica.getCodigo()`.
- Búsqueda de bolsines de la CM origen y enviados mediante `Bolsin`, `CambioEstadoBolsin` y `Estado`. El historial es la única fuente del estado vigente; cero o varios cambios actuales producen una inconsistencia HTTP 409.
- Boundary `InterfazGPSTracker` y Adapter para XTR-4500L, NavTrack QX-7A y GeoPulse MTR-900. La boundary crea como máximo un adaptador por modelo presente en cada consulta y lo reutiliza durante el recorrido, sin consultar repositorios.
- Contrato con arreglos paralelos de números, modelos y códigos de CM destino, más un código único de CM origen. Retorno `String[][]` de cinco columnas: `[numeroBolsin, latitud, longitud, fechaHoraActualizacion, errorGps]`, conservando el orden y admitiendo fallos parciales. Las ubicaciones ausentes usan `null`, nunca coordenadas cero.
- XTR interpreta JSON con `items`, NavTrack texto CSV y GeoPulse una matriz JSON, según el [simulador de la cátedra](https://github.com/LiberaeMentis/dsi-api-bolsines/blob/main/README.md). Se verifica el número recibido antes de aceptar una ubicación.
- Convención UTC para fechas externas sin zona; las fechas con desplazamiento lo conservan. Es una convención del backend, no una zona informada por el simulador.
- Persistencia JPA con H2 de archivo, filtros por precinto y código de CM destino, y datos demo para los bolsines 101, 102 y 103.
- **Verificación:** compilación y 31 pruebas aprobadas. Integración HTTP verificada el 15/11/2026 con el simulador: consulta mixta 101/XTR, 102/NavTrack y 103/GeoPulse con números, coordenadas y fechas coincidentes con las respuestas directas; fallo parcial con 104 y 106 exitosos y 999 inexistente (GPS 404, backend 200 con ubicación nula y error).
- La verificación HTTP usó una copia temporal sin cambios del simulador, puertos 18080/18081 y una base H2 en memoria. La fixture del backend agrupó los tres modelos bajo una CM para probar una consulta mixta; no se modificó la base existente. Las pruebas automatizadas GPS usan respuestas simuladas y son independientes de esa verificación HTTP manual.
- **Próximo arranque sobre una base anterior:** migración del estado guardado al historial, conservando registros y sin inventar fechas históricas; detalles debajo.
- **Pendientes:** mapa/frontend, implementación de notificaciones (el proveedor actual devuelve 501 al intentar enviar con datos válidos), actualización de los diagramas y revisión restante del ERS. La autenticación real queda fuera de esta etapa demo.

## Iniciar

1. Instalar JDK 21 y Maven 3.9+; abrir esta carpeta en VS Code.
2. En otra carpeta clonar el mock `https://github.com/LiberaeMentis/dsi-api-bolsines` y ejecutarlo con `./mvnw.cmd spring-boot:run` en Windows. Funciona en `localhost:8080`.
3. En **este** proyecto ejecutar `mvn test` y luego `mvn spring-boot:run '-Dspring-boot.run.profiles=demo'` en PowerShell. Funciona en `localhost:8081`.
4. Navegar a:
   - http://localhost:8081/api/seguimiento?usuario=encargado-cba (XTR, bolsín 101)
   - http://localhost:8081/api/seguimiento?usuario=encargado-ros (NavTrack, bolsín 102)
   - http://localhost:8081/api/seguimiento?usuario=encargado-mdz (GeoPulse, bolsín 103)

**Base existente:** no eliminar ni reiniciar la base para este cambio. Al iniciar, Hibernate agrega las tablas del historial y `MigracionEstadoBolsin` importa una sola vez el estado anterior, antes de cargar los datos demo. Conserva los bolsines, sus asociaciones y la columna legada `bolsin.estado` como archivo; esa columna deja de ser obligatoria y no se usa para determinar el estado vigente.

Cada estado importado genera un `CambioEstadoBolsin` abierto, con `importado=true` y `fechaHoraInicio=null`: se conoce el estado guardado, pero no desde cuando rige. No se reconstruyen transiciones ni fechas historicas. La marca `migracion_estado_bolsin.version=1` evita repetir la importacion o reparar silenciosamente inconsistencias posteriores. Si un bolsin ya tiene historial, la importacion no lo reemplaza. Los datos demo nuevos registran la fecha real de su creacion.

El historial es la unica fuente del estado vigente: `fechaHoraFin == null` identifica un cambio actual y debe existir exactamente uno por bolsin. Cero o varios cambios actuales producen un error explicito HTTP 409 al consultar ese bolsin en el flujo. La migracion esta implementada para H2, la base configurada del proyecto. Antes del primer arranque con una base existente, detener la aplicacion y conservar una copia del archivo H2 como respaldo; no ejecutar simultaneamente dos instancias durante la migracion.

La importacion conserva los codigos de CM existentes: no corrige automaticamente codigos de fixtures antiguos ni los reemplaza por los del simulador.

## Organización

- `adapter/IGpsTracker`: interfaz Target
- `adapter/Adaptador*`: interpreta cada formato externo y devuelve la matriz normalizada de cinco columnas
- `adapter/InterfazGPSTracker`: boundary que valida los arreglos y crea/reutiliza los adaptadores por consulta
- `service/GestorSeguimientoBolsin`: coordina el CU, construye los arreglos y consume el contrato normalizado sin interpretar formatos externos
- `domain/` + `repository/`: entidades y almacenamiento persistente
- `controller/`: API REST para el frontend
- `INSTALACION_WINDOWS.md`: instrucciones locales
- `CONTRATO_FRONTEND.md`: acuerdo de DTO y endpoints con el compañero
- `CONTRATO_GPS.md`: firmas, columnas de la matriz, formatos externos, orden y fallos parciales

**Fuentes:** ERS CU36 (PDF del curso) y README del repositorio oficial GPS.

## Trabajar en equipo

Clonar este repositorio y abrir su carpeta en VS Code. Cada integrante necesita JDK 21 y Maven 3.9+, además del simulador oficial en otra carpeta. Las instrucciones de inicio anteriores no dependen de las rutas de la computadora original.

Antes de empezar una tarea, ejecutar `git pull`. Trabajar en una rama propia, subirla y proponer un pull request para revisar los cambios antes de unirlos a `main`.

No se incluyen la base de datos de demostración, archivos compilados, herramientas instaladas ni configuraciones personales de VS Code. La base de datos se crea al ejecutar el perfil demo. Las claves incluidas son exclusivamente las claves mock publicadas por la cátedra.
