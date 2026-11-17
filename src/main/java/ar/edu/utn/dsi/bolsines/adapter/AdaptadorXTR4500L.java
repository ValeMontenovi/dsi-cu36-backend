package ar.edu.utn.dsi.bolsines.adapter;

import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import java.util.Map;

public class AdaptadorXTR4500L extends AdaptadorGps {

    public AdaptadorXTR4500L(ClienteGpsHttp cliente, String path, String claveApi) {
        super(cliente, path, claveApi);
    }

    @Override
    protected String solicitarLocalizacion(int numero, String origen, String destino) {
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

    @Override
    protected UbicacionBolsin adaptarDatosRelevados(String respuesta) {
        return normalizador.desdeJson(respuesta);
    }
}
