package ar.edu.utn.dsi.bolsines.adapter;

import ar.edu.utn.dsi.bolsines.domain.ModeloGps;
import java.util.EnumMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InterfazGPSTracker {

    private final ClienteGpsHttp cliente;
    private final String xtrPath, xtrClave, navPath, navClave, geoPath, geoClave;

    public InterfazGPSTracker(
        ClienteGpsHttp cliente,
        @Value("${tracker.xtr-path}") String xtrPath,
        @Value("${tracker.xtr-api-key}") String xtrClave,
        @Value("${tracker.navtrack-path}") String navPath,
        @Value("${tracker.navtrack-api-key}") String navClave,
        @Value("${tracker.geopulse-path}") String geoPath,
        @Value("${tracker.geopulse-api-key}") String geoClave
    ) {
        this.cliente = cliente;
        this.xtrPath = xtrPath;
        this.xtrClave = xtrClave;
        this.navPath = navPath;
        this.navClave = navClave;
        this.geoPath = geoPath;
        this.geoClave = geoClave;
    }

    public String[][] obtenerLocalizacion(
        int[] numeros,
        String[] modelos,
        String origen,
        String[] destinos
    ) {
        if (
            numeros == null ||
            modelos == null ||
            destinos == null ||
            numeros.length != modelos.length ||
            numeros.length != destinos.length
        ) throw new IllegalArgumentException(
            "Los tres arreglos GPS deben tener igual longitud y no ser nulos"
        );
        ModeloGps[] tipos = new ModeloGps[modelos.length];
        // Validar el contrato completo antes de efectuar consultas externas.
        for (int i = 0; i < modelos.length; i++) {
            if (modelos[i] == null) throw new IllegalArgumentException(
                "Modelo GPS nulo en posicion " + i
            );
            try {
                tipos[i] = ModeloGps.valueOf(modelos[i]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Modelo GPS desconocido: " + modelos[i], e);
            }
        }
        var adaptadores = new EnumMap<ModeloGps, IGpsTracker>(ModeloGps.class);
        String[][] resultado = new String[numeros.length][5];
        for (int i = 0; i < numeros.length; i++) {
            IGpsTracker adaptador = adaptadores.computeIfAbsent(tipos[i], this::crearAdaptador);
            resultado[i] = adaptador.recibirDatosLocalizacion(
                new int[] { numeros[i] },
                origen,
                new String[] { destinos[i] }
            )[0];
        }
        return resultado;
    }

    protected IGpsTracker crearAdaptador(ModeloGps modelo) {
        return switch (modelo) {
            case XTR_4500L -> new AdaptadorXTR4500L(cliente, xtrPath, xtrClave);
            case NAVTRACK_QX_7A -> new AdaptadorNavTrackQX7A(cliente, navPath, navClave);
            case GEOPULSE_MTR_900 -> new AdaptadorGeoPulseMTR900(cliente, geoPath, geoClave);
        };
    }
}
