package ar.edu.utn.dsi.bolsines.dto;
import java.time.OffsetDateTime;
public record UbicacionBolsin(Long numeroBolsin,double latitud,double longitud,OffsetDateTime fechaHoraActualizacion) {}
