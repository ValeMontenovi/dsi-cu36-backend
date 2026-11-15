package ar.edu.utn.dsi.bolsines.adapter;

import java.util.Map;
import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;

public class AdaptadorNavTrackQX7A extends AdaptadorGps {
    public AdaptadorNavTrackQX7A(ClienteGpsHttp cliente, String path, String claveApi) { super(cliente,path,claveApi); }
    @Override protected String solicitarLocalizacion(int numero, String origen, String destino) {
        return cliente.get(path,claveApi,Map.of("numeroBolsin",Integer.toString(numero),"codigoComisionMedicaDestino",requerido(destino,"CM destino")));
    }
    @Override protected UbicacionBolsin adaptarDatosRelevados(String respuesta) {
        return normalizador.desdeCsv(respuesta);
    }
}
