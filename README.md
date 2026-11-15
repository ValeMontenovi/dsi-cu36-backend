# DSI Bolsines — CU 36 | Backend Java Spring Boot

Backend académico del CU 36 **Consultar seguimiento de bolsines**, con patrón GoF **Adapter**.

## Estado

- Adaptadores de XTR-4500L, NavTrack QX-7A y GeoPulse MTR-900 conectados según las rutas, parámetros, headers y formatos **verificados** en el README oficial de [LiberaeMentis/dsi-api-bolsines](https://github.com/LiberaeMentis/dsi-api-bolsines/blob/main/README.md).
- Gestor, entidades y persistencia JPA H2 de archivo; filtros por precinto/destino; control básico de pertenencia de CM.
- Datos demo alineados con los bolsines 101, 102, 103 del simulador y tres usuarios para probar los adaptadores.
- Tests unitarios creados (normalizador y reglas básicas de gestor).
- **Verificado localmente**: compilación Maven y 7 pruebas aprobadas; los tres endpoints GPS y las tres consultas del backend respondieron HTTP 200 con coordenadas coincidentes y sin error GPS.
- **Pendiente**: configurar un servicio de correo real (actualmente responde 501); autenticación real (usuario por query es solo demo); integración de frontend y revisión completa del ERS.

## Iniciar

1. Instalar JDK 21 y Maven 3.9+; abrir esta carpeta en VS Code.
2. En otra carpeta clonar el mock `https://github.com/LiberaeMentis/dsi-api-bolsines` y ejecutarlo con `./mvnw.cmd spring-boot:run` en Windows. Funciona en `localhost:8080`.
3. En **este** proyecto ejecutar `mvn test` y luego `mvn spring-boot:run '-Dspring-boot.run.profiles=demo'` en PowerShell. Funciona en `localhost:8081`.
4. Navegar a:
   - http://localhost:8081/api/seguimiento?usuario=encargado-cba (XTR, bolsín 101)
   - http://localhost:8081/api/seguimiento?usuario=encargado-ros (NavTrack, bolsín 102)
   - http://localhost:8081/api/seguimiento?usuario=encargado-mdz (GeoPulse, bolsín 103)

**Precaución:** si ya usaste la versión anterior del proyecto, eliminá únicamente tu archivo demo local `data/bolsines.mv.db` cuando la aplicación esté detenida, o usa una ruta nueva de base H2. La versión anterior sembraba CM con códigos CBA/ROS y esos registros no equivalen a los identificadores oficiales del mock.

## Organización

- `adapter/IGpsTracker`: interfaz Target
- `adapter/*Adapter`: traduce cada API externa a un mismo DTO `UbicacionBolsin`
- `adapter/SelectorGpsTracker`: resuelve qué adaptador corresponde
- `service/GestorSeguimientoBolsin`: cliente que opera contra la abstracción
- `domain/` + `repository/`: entidades y almacenamiento persistente
- `controller/`: API REST para el frontend
- `INSTALACION_WINDOWS.md`: instrucciones locales
- `CONTRATO_FRONTEND.md`: acuerdo de DTO y endpoints con el compañero

**Fuentes:** ERS CU36 (PDF del curso) y README del repositorio oficial GPS.

## Trabajar en equipo

Clonar este repositorio y abrir su carpeta en VS Code. Cada integrante necesita JDK 21 y Maven 3.9+, además del simulador oficial en otra carpeta. Las instrucciones de inicio anteriores no dependen de las rutas de la computadora original.

Antes de empezar una tarea, ejecutar `git pull`. Trabajar en una rama propia, subirla y proponer un pull request para revisar los cambios antes de unirlos a `main`.

No se incluyen la base de datos de demostración, archivos compilados, herramientas instaladas ni configuraciones personales de VS Code. La base de datos se crea al ejecutar el perfil demo. Las claves incluidas son exclusivamente las claves mock publicadas por la cátedra.
