# Instrucciones para Windows + VS Code (backend CU 36)

## 1. Instalar herramientas

1. Instalar **JDK 21** (por ejemplo Eclipse Temurin 21) desde https://adoptium.net/ .
2. Instalar **VS Code** desde https://code.visualstudio.com/ si no lo tenés.
3. Instalar **Maven 3.9+** desde https://maven.apache.org/download.cgi (o usar Maven Wrapper si luego se incorpora). Verificar su carpeta `bin` en PATH.
4. Abrir en VS Code la **carpeta del repositorio** (la que contiene `backend/`, `frontend/` y `api/`; no abrir solamente el archivo ZIP). VS Code recomendará las extensiones del proyecto: `Extension Pack for Java` y `Spring Boot Extension Pack`. Aceptarlas.

No compartas claves API, contraseñas ni tokens por chat.

## 2. Comprobar herramientas (terminal PowerShell de VS Code)

```powershell
java -version
javac -version
mvn -version
```

Deben detectar un JDK 21 (no solamente JRE) y Maven.

## 3. Verificar pruebas y ejecutar

Desde la carpeta `backend/`:

```powershell
cd backend
mvn test
mvn spring-boot:run '-Dspring-boot.run.profiles=demo'
```

Abrir en navegador:

`http://localhost:8081/api/seguimiento?usuario=encargado-cba`

**Esperable con simulador en funcionamiento:** coordenadas reales del mock. Si hay errores en `errorGps`, comprobar que el simulador esté activo en el puerto 8080.

## 4. Integración con simulador provisto por la cátedra

El simulador `https://github.com/LiberaeMentis/dsi-api-bolsines` está en la carpeta `api/` del repositorio (submódulo). Si está vacía, ejecutar `git submodule update --init` desde la raíz. Abrir su README y ejecutar el simulador siguiendo sus instrucciones. Verificar rutas HTTP, formatos, parámetros, clave, headers y datos de prueba.

Las rutas y los headers ya están alineados con el README del repositorio de la cátedra. Las claves demo vienen preconfiguradas; se pueden sobrescribir con `GPS_XTR_API_KEY`, `GPS_NAVTRACK_API_KEY` y `GPS_GEOPULSE_API_KEY`.

Ejemplo de variable temporal PowerShell si el mock cambia de puerto:

```powershell
$env:GPS_BASE_URL = "http://localhost:8080"
```

## 5. Problemas frecuentes

- `java` / `javac` no reconocido: reiniciar VS Code después de instalar JDK, revisar PATH y JAVA_HOME.
- `mvn` no reconocido: instalar Maven y agregar `bin` al PATH.
- Puerto 8081 ocupado: cambiar `server.port` en `src/main/resources/application.yml`.
- GPS no responde: verificar que el simulador esté levantado y que la URL/rutas/headers coincidan con el README.
- `POST /notificar` devuelve 501: proveedor real de email pendiente, no es un envío exitoso.
