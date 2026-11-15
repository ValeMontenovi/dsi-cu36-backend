package ar.edu.utn.dsi.bolsines.adapter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ar.edu.utn.dsi.bolsines.domain.Bolsin;
import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
@Component public class AdaptadorXTR4500L implements IGpsTracker {
 private final ClienteGpsHttp cliente;private final String path;private final String apiKey;private final NormalizadorGps normalizador=new NormalizadorGps();
 public AdaptadorXTR4500L(ClienteGpsHttp cliente,@Value("${tracker.xtr-path}") String path,@Value("${tracker.xtr-api-key}") String apiKey){this.cliente=cliente;this.path=path;this.apiKey=apiKey;}
 @Override public UbicacionBolsin obtenerUbicacion(Bolsin b){return normalizador.desdeJson(cliente.get(path,apiKey,Map.of("numeroBolsin",b.getNroBolsin().toString(),"codigoComisionMedicaOrigen",b.getOrigen().getCodigo())));}
}
