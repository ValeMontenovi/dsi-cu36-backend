package ar.edu.utn.dsi.bolsines.dto;

public record SeguimientoBolsin(
    Long numeroBolsin,
    String numeroPrecinto,
    String cmOrigen,
    String cmDestino,
    String modeloGps,
    UbicacionBolsin ubicacion,
    String errorGps
) {}
