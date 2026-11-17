# DSI Bolsines — CU 36 | Frontend (React + Vite)

Frontend del CU 36 **Consultar seguimiento de bolsines**. Consume el backend de este mismo
repositorio (la carpeta raíz) según [`CONTRATO_FRONTEND.md`](../CONTRATO_FRONTEND.md).

## Requisitos

- Node.js 20+ (`node -v`)
- Backend corriendo en `http://localhost:8081` (perfil `demo`, desde la carpeta raíz del repositorio)
- Simulador GPS de la cátedra corriendo en `http://localhost:8080`
  ([LiberaeMentis/dsi-api-bolsines](https://github.com/LiberaeMentis/dsi-api-bolsines), se clona aparte)

## Iniciar

Desde esta carpeta (`frontend/`):

```powershell
npm install
npm run dev
```

Abrir http://localhost:5173

Vite reenvía todo lo que empieza con `/api` al backend (ver `vite.config.js`), así que en el código
se usan rutas relativas como `/api/seguimiento`.

## Estructura

Las carpetas siguen las capas del diagrama de secuencia (boundary, control, entidad).
Las clases y sus métodos tienen los mismos nombres que en el diagrama.

```
src/
  main.jsx                              arranca React y monta la Aplicacion
  boundary/
    Aplicacion.jsx                      decide qué pantalla se ve: inicio de sesión, menú o CU 36
    PantallaInicioSesion.jsx            CU 5 Iniciar sesión: mail y contraseña
    PantallaMenuPrincipal.jsx           opciones (casos de uso de la ERS) del perfil logueado
    PantallaSeguimientoBolsin.jsx       pantalla del actor: registrarSeguimientoBolsin(), habilitarPantalla(),
                                        mostrarCMUsuario(), mostrarMapaBolsin(), solicitarSelBolsinTipo(),
                                        tomarSeleccionBolsin(), solicitarEnvioMail(), tomarConfirmacion()
    InterfazGoogleMaps.js               frontera con el servicio de mapas: recibirUbicacionBolsin(), mostrarMaps()
  control/
    GestorInicioSesion.js               iniciarSesion(), buscarOpcionesDelUsuario(), cerrarSesion()
    GestorSeguimientoBolsin.js          parte del gestor que corre en el navegador:
                                        registrarSeguimientoBolsin(), solicitarMapa(), tomarSelecBolsin(),
                                        regConfirmacion(), finCU()
  entidad/
    Sesion.js                           sesión activa (Activa:Sesion): getUsuario(), getUsuarioLogueado()
    Usuario.js                          usuario que inicia sesión: esTuMail(), validarContrasena()
  servicios/
    usuariosRegistrados.js              usuarios de prueba (mail, contraseña, perfil, CM)
    opcionesDelSistema.js               opciones del menú, sacadas del listado de CU de la ERS
    backendSeguimiento.js               único lugar con pedidos HTTP (GET /api/seguimiento y
                                        POST /api/seguimiento/{numero}/notificar)
  vistas/
    TablaBolsines.jsx                   bolsines como etiquetas de envío (precinto, recorrido,
                                        modelo de GPS, adaptador usado y ubicación)
    MapaBolsines.jsx                    mapa Leaflet + OpenStreetMap con un pin amarillo por bolsín
                                        (el del bolsín elegido se resalta)
    SeleccionBolsin.jsx                 barra para elegir un bolsín y continuar (solicitarSelBolsinTipo)
    ConfirmacionEnvioMail.jsx           ventana para confirmar el envío del mail (solicitarEnvioMail)
    Iconos.jsx                          logo del bolsín en SVG
    IconosMenu.jsx                      un ícono SVG por opción del menú
    DibujoRedComisiones.jsx             dibujo del inicio de sesión: CM, recorridos y un bolsín viajando
    CicloDeVidaBolsin.jsx               gráfico de los estados de un bolsín (ERS, reglas de negocio)
    BarraSuperior.jsx                   barra azul con el usuario logueado
    formato.js                          formato de fechas y coordenadas
  estilos/
    index.css                           colores, letra Barlow, etiquetas, pines y animaciones
```

La letra Barlow se instala con npm (`@fontsource/barlow` y `@fontsource/barlow-condensed`),
así la pantalla se ve igual aunque no haya internet.

El patrón Adapter está en el backend (`InterfazGPSTracker` y los tres adaptadores). El frontend
muestra su resultado: la ubicación de cada bolsín y qué adaptador la obtuvo.

## Inicio de sesión y menú principal

La aplicación arranca en **Iniciar sesión** (CU 5 de la ERS). Con el mail y la contraseña correctos se
abre el **menú principal** con todas las opciones que la ERS le da al perfil del usuario. Solo funcionan:

- **Consultar seguimiento de bolsines (CU 36)**: abre la pantalla del caso de uso, que arranca sola con
  la sesión del usuario logueado.
- **Cerrar sesión (CU 6)**: vuelve al inicio de sesión.

Las demás opciones se ven con la marca "Próximamente". La sesión se recuerda mientras la pestaña esté
abierta, así que recargar la página no te saca del sistema.

### Usuarios de prueba

Todos usan la contraseña `Bolsines2026`.

| Mail                               | Perfil                     | CM        | Usuario en el backend |
|------------------------------------|----------------------------|-----------|-----------------------|
| `encargado.cordoba@bolsines.test`  | Encargado de Bolsines      | Córdoba   | `encargado-cba`       |
| `encargado.rosario@bolsines.test`  | Encargado de Bolsines      | Rosario   | `encargado-ros`       |
| `encargado.mendoza@bolsines.test`  | Encargado de Bolsines      | Mendoza   | `encargado-mdz`       |
| `gerente.cordoba@bolsines.test`    | Gerente de Comisión Médica | Córdoba   | (no existe)           |

El gerente sirve para ver que cada perfil tiene otras opciones: no tiene el CU 36.

**Importante:** el backend todavía no tiene inicio de sesión, así que los usuarios y contraseñas se
validan en el navegador (`src/servicios/usuariosRegistrados.js`). Alcanza para el PPAI, pero en un
sistema real la contraseña la tiene que validar el backend.

## Scripts

| Comando           | Qué hace                                  |
|-------------------|-------------------------------------------|
| `npm run dev`     | servidor de desarrollo con recarga en vivo |
| `npm run build`   | genera la versión de producción en `dist/` |
| `npm run preview` | sirve la carpeta `dist/` para probarla     |
| `npm run lint`    | revisa el código con oxlint               |

## Selección del bolsín y envío del mail (resto del diagrama)

1. Después de `mostrarMapaBolsin()` el gestor llama a `solicitarSelBolsinTipo()`: cada etiqueta de la
   lista tiene un botón de opción. Solo se pueden elegir bolsines con ubicación.
2. Con **Continuar** la pantalla ejecuta `tomarSeleccionBolsin()` → gestor `tomarSelecBolsin()`.
3. El gestor llama a `solicitarEnvioMail()`: se abre la ventana de confirmación.
4. **Confirmar envío** ejecuta `tomarConfirmacion()` → gestor `regConfirmacion()`, que hace
   `POST /api/seguimiento/{numero}/notificar`. En el backend van `buscarMail()`, el bucle de empleados y
   `llamarCU31/NotificarUbicacionBolsin()`. Con `204` el gestor ejecuta `finCU()` y se muestra
   "Ubicación enviada". **Cancelar** (o Escape) vuelve a la selección.

## Pendiente en el backend

- `/notificar` hoy responde `501` porque no está configurado el proveedor de correo (CU 31). La pantalla
  lo muestra como error; nunca como enviado.
