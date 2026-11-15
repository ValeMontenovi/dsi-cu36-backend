package ar.edu.utn.dsi.bolsines.adapter;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
/** Transformaciones independientes del transporte HTTP. Los nombres reales de campos deben validarse con el README. */
public class NormalizadorGps {
 private static final ObjectMapper MAPPER=new ObjectMapper();
 public UbicacionBolsin desdeJson(String response){try {JsonNode root=MAPPER.readTree(response); if(root.isArray()){root=root.get(0);} else if(root.has("items")){root=root.get("items");if(root.isArray())root=root.get(0);} else if(root.has("data")){root=root.get("data");if(root.isArray())root=root.get(0);}return node(root);}catch(Exception e){throw new GpsException("JSON GPS inválido o no soportado",e);}}
 public UbicacionBolsin desdeCsv(String response){try {String[] f=response.replace("\"","").split(",",4);if(f.length!=4)throw new IllegalArgumentException("Se esperaban 4 campos");return crear(Long.parseLong(f[0].trim()),Double.parseDouble(f[1].trim()),Double.parseDouble(f[2].trim()),f[3].trim());}catch(Exception e){throw new GpsException("Respuesta NavTrack inválida",e);}}
 public UbicacionBolsin desdeMatriz(String response){try {JsonNode r=MAPPER.readTree(response);if(r.has("data"))r=r.get("data");if(!r.isArray()||r.size()==0||!r.get(0).isArray())throw new IllegalArgumentException("Se esperaba matriz");JsonNode a=r.get(0);return crear(a.get(0).asLong(),a.get(1).asDouble(),a.get(2).asDouble(),a.get(3).asText());}catch(Exception e){throw new GpsException("Respuesta GeoPulse inválida",e);}}
 private UbicacionBolsin node(JsonNode n){if(n==null)throw new IllegalArgumentException("Sin ubicaciones");return crear(field(n,"numeroBolsin","bolsinNumber","bolsinId","numero").asLong(),field(n,"latitud","latitude","lat").asDouble(),field(n,"longitud","longitude","lon","lng").asDouble(),field(n,"fechaHoraUltimaActualizacion","fechaHoraActualizacion","lastUpdate","timestamp","fechaHora","updatedAt").asText());}
 private JsonNode field(JsonNode n,String...keys){for(String k:keys)if(n.hasNonNull(k))return n.get(k);throw new IllegalArgumentException("Falta campo GPS: "+List.of(keys));}
 private UbicacionBolsin crear(Long numero,double lat,double lon,String fecha){if(!Double.isFinite(lat)||!Double.isFinite(lon)||lat < -90||lat>90||lon < -180||lon>180)throw new IllegalArgumentException("Coordenadas inválidas");OffsetDateTime dt;try{dt=OffsetDateTime.parse(fecha);}catch(Exception ex){dt=LocalDateTime.parse(fecha).atOffset(ZoneOffset.UTC);}return new UbicacionBolsin(numero,lat,lon,dt);}
}
