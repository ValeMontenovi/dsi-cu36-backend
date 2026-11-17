package ar.edu.utn.dsi.bolsines.adapter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NormalizadorGpsTest {

    NormalizadorGps n = new NormalizadorGps();

    @Test
    void xtrJson() {
        var u = n.desdeJson(
            "{\"items\":[{\"numeroBolsin\":101,\"latitud\":-31.42,\"longitud\":-64.18,\"fechaHoraUltimaActualizacion\":\"2026-11-15T11:00:00Z\"}]}"
        );
        assertEquals(101, u.numeroBolsin());
        assertEquals(-31.42, u.latitud());
    }

    @Test
    void navtrackCsv() {
        var u = n.desdeCsv("102,-32.95,-60.65,2026-11-15T11:00:00Z");
        assertEquals(102, u.numeroBolsin());
    }

    @Test
    void geoMatriz() {
        var u = n.desdeMatriz("[[103,-31.42,-64.18,\"2026-11-15T11:00:00Z\"]]");
        assertEquals(103, u.numeroBolsin());
    }

    @Test
    void invalid() {
        assertThrows(GpsException.class, () -> n.desdeCsv("n/a"));
    }
}
