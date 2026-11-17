package ar.edu.utn.dsi.bolsines.adapter;

import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Formatos del simulador oficial. Fechas sin zona conservan la convencion UTC existente. */
public class NormalizadorGps {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public UbicacionBolsin desdeJson(String respuesta) {
        try {
            JsonNode items = MAPPER.readTree(respuesta).get("items");
            if (
                items == null || !items.isArray() || items.size() != 1
            ) throw new IllegalArgumentException("Se esperaba un item XTR");
            JsonNode n = items.get(0);
            return crear(
                n.get("numeroBolsin"),
                n.get("latitud"),
                n.get("longitud"),
                n.get("fechaHoraUltimaActualizacion")
            );
        } catch (Exception e) {
            throw new GpsException("JSON GPS invalido o no soportado", e);
        }
    }

    public UbicacionBolsin desdeCsv(String respuesta) {
        try {
            String[] f = respuesta.trim().split(",", -1);
            if (f.length != 4) throw new IllegalArgumentException(
                "Se esperaban cuatro campos NavTrack"
            );
            return crear(
                Long.parseLong(f[0].trim()),
                Double.parseDouble(f[1].trim()),
                Double.parseDouble(f[2].trim()),
                f[3].trim()
            );
        } catch (Exception e) {
            throw new GpsException("Respuesta NavTrack invalida", e);
        }
    }

    public UbicacionBolsin desdeMatriz(String respuesta) {
        try {
            JsonNode matriz = MAPPER.readTree(respuesta);
            if (
                !matriz.isArray() ||
                matriz.size() != 1 ||
                !matriz.get(0).isArray() ||
                matriz.get(0).size() != 4
            ) throw new IllegalArgumentException(
                "Se esperaba una fila GeoPulse de cuatro columnas"
            );
            JsonNode a = matriz.get(0);
            return crear(a.get(0), a.get(1), a.get(2), a.get(3));
        } catch (Exception e) {
            throw new GpsException("Respuesta GeoPulse invalida", e);
        }
    }

    private UbicacionBolsin crear(JsonNode numero, JsonNode lat, JsonNode lon, JsonNode fecha) {
        if (
            numero == null ||
            !numero.isIntegralNumber() ||
            !numero.canConvertToLong() ||
            lat == null ||
            !lat.isNumber() ||
            lon == null ||
            !lon.isNumber() ||
            fecha == null ||
            !fecha.isTextual()
        ) throw new IllegalArgumentException("Campos GPS ausentes o de tipo incorrecto");
        return crear(numero.longValue(), lat.doubleValue(), lon.doubleValue(), fecha.textValue());
    }

    private UbicacionBolsin crear(long numero, double lat, double lon, String fecha) {
        if (
            !Double.isFinite(lat) ||
            !Double.isFinite(lon) ||
            lat < -90 ||
            lat > 90 ||
            lon < -180 ||
            lon > 180
        ) throw new IllegalArgumentException("Coordenadas invalidas");
        OffsetDateTime dt;
        try {
            dt = OffsetDateTime.parse(fecha);
        } catch (java.time.format.DateTimeParseException e) {
            dt = LocalDateTime.parse(fecha).atOffset(ZoneOffset.UTC);
        }
        return new UbicacionBolsin(numero, lat, lon, dt);
    }
}
