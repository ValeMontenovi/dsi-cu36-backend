package ar.edu.utn.dsi.bolsines.adapter;

import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class AdaptadorXTR4500L implements IAdaptadorGPSTracker {

    private final ClienteGpsHttp cliente;
    private final String path;
    private final String claveApi;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public AdaptadorXTR4500L(ClienteGpsHttp cliente, String path, String claveApi) {
        this.cliente = cliente;
        this.path = path;
        this.claveApi = claveApi;
    }

    @Override
    public String[][] recibirDatosLocalizacion(int[] numeros, String origen, String[] destinos) {
        if (
            numeros == null || destinos == null || numeros.length != destinos.length
        ) throw new IllegalArgumentException(
            "Numeros y destinos GPS deben tener igual longitud y no ser nulos"
        );
        String[][] filas = new String[numeros.length][5];
        for (int i = 0; i < numeros.length; i++) {
            filas[i][0] = Integer.toString(numeros[i]);
            try {
                UbicacionBolsin u = adaptarDatosRelevados(
                    solicitarLocalizacion(numeros[i], origen, destinos[i])
                );
                if (u.numeroBolsin() != numeros[i]) throw new GpsException(
                    "Respuesta GPS corresponde a otro bolsin"
                );
                filas[i][1] = Double.toString(u.latitud());
                filas[i][2] = Double.toString(u.longitud());
                filas[i][3] = u.fechaHoraActualizacion().toString();
            } catch (GpsException | IllegalArgumentException e) {
                filas[i] = new String[] {
                    Integer.toString(numeros[i]),
                    null,
                    null,
                    null,
                    e.getMessage(),
                };
            }
        }
        return filas;
    }

    private static String requerido(String valor, String nombre) {
        if (valor == null || valor.isBlank()) throw new GpsException("Falta " + nombre);
        return valor;
    }

    private String solicitarLocalizacion(int numero, String origen, String destino) {
        return cliente.get(
            path,
            claveApi,
            Map.of(
                "numeroBolsin",
                Integer.toString(numero),
                "codigoComisionMedicaOrigen",
                requerido(origen, "CM origen")
            )
        );
    }

    private UbicacionBolsin adaptarDatosRelevados(String respuesta) {
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

    // Las fechas externas sin zona conservan la convencion UTC acordada.
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
