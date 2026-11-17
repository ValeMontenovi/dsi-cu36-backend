# DSI Bolsines — CU 36 Consultar seguimiento de bolsines

Proyecto Práctico de Aplicación Integrador (PPAI) de Diseño de Sistemas de Información, UTN FRC 2026.

## Estructura

```
backend/    API REST en Java 21 + Spring Boot (puerto 8081). Patrón Adapter para los GPS.
frontend/   Pantallas en React + Vite + Leaflet (puerto 5173).
api/        Simulador GPS de la cátedra (puerto 8080). Es un submódulo: apunta al repositorio
            LiberaeMentis/dsi-api-bolsines, no es código de este proyecto.
```

En la raíz quedan los archivos compartidos: este README, `CONTRATO_FRONTEND.md` (acuerdo entre
backend y frontend), la configuración de formato (Prettier) y la de VS Code.

## Clonar

El simulador es un submódulo, así que hay que clonar con `--recurse-submodules`:

```powershell
git clone --recurse-submodules https://github.com/ValeMontenovi/dsi-cu36-backend.git
```

Si ya lo clonaste sin esa opción y la carpeta `api/` está vacía:

```powershell
git submodule update --init
```

## Levantar todo (tres terminales)

Requisitos: JDK 21, Maven 3.9+ y Node.js 20+.

```powershell
# 1. Simulador GPS (puerto 8080)
cd api
./mvnw.cmd spring-boot:run

# 2. Backend (puerto 8081)
cd backend
mvn spring-boot:run '-Dspring-boot.run.profiles=demo'

# 3. Frontend (puerto 5173)
cd frontend
npm install      # solo la primera vez
npm run dev
```

Abrir http://localhost:5173 e iniciar sesión con uno de los usuarios de prueba de
`frontend/README.md`.

Más detalle en [`backend/README.md`](backend/README.md),
[`backend/INSTALACION_WINDOWS.md`](backend/INSTALACION_WINDOWS.md) y
[`frontend/README.md`](frontend/README.md).

## Trabajar en equipo

Antes de empezar una tarea, ejecutar `git pull` (y `git submodule update --init` si cambió la
versión del simulador).

Para aplicar el formato con Prettier (requiere Node.js y npm), ejecutar `npm ci` y luego
`npm run format` desde la raíz. Para verificarlo sin modificar archivos, usar `npm run format:check`.
La carpeta `api/` no se formatea porque es código de la cátedra.
