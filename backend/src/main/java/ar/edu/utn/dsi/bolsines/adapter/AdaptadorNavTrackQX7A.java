package ar.edu.utn.dsi.bolsines.adapter;

import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import java.util.Map;

public class AdaptadorNavTrackQX7A implements IAdaptadorGPSTracker {

    private final ClienteGpsHttp cliente;
    private final String path;
    private final String claveApi;
    private final NormalizadorGps normalizador = new NormalizadorGps();

    public AdaptadorNavTrackQX7A(ClienteGpsHttp cliente, String path, String claveApi) {
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
                "codigoComisionMedicaDestino",
                requerido(destino, "CM destino")
            )
        );
    }

    private UbicacionBolsin adaptarDatosRelevados(String respuesta) {
        return normalizador.desdeCsv(respuesta);
    }
}
