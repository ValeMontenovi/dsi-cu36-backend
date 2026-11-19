package ar.edu.utn.dsi.bolsines.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

class AdaptadoresFormatoGpsTest {

    private final ClienteGpsHttp cliente = mock(ClienteGpsHttp.class);

    private String[] consultar(IAdaptadorGPSTracker adaptador, int numero, String respuesta) {
        when(cliente.get(anyString(), anyString(), anyMap())).thenReturn(respuesta);
        return adaptador.recibirDatosLocalizacion(new int[] { numero }, "ORIGEN", new String[] {
            "DESTINO",
        })[0];
    }

    @Test
    void xtrInterpretaJsonYConservaDesplazamiento() {
        var fila = consultar(
            new AdaptadorXTR4500L(cliente, "/xtr", "k"),
            101,
            "{\"items\":[{\"numeroBolsin\":101,\"latitud\":-31.42,\"longitud\":-64.18,\"fechaHoraUltimaActualizacion\":\"2026-11-15T11:00:00-03:00\"}]}"
        );
        assertArrayEquals(
            new String[] { "101", "-31.42", "-64.18", "2026-11-15T11:00-03:00", null },
            fila
        );
    }

    @Test
    void navtrackInterpretaCsvYAplicaUtcSinZona() {
        var fila = consultar(
            new AdaptadorNavTrackQX7A(cliente, "/nav", "k"),
            102,
            "102,-32.95,-60.65,2026-11-15T11:00:00"
        );
        assertArrayEquals(
            new String[] { "102", "-32.95", "-60.65", "2026-11-15T11:00Z", null },
            fila
        );
    }

    @Test
    void geoPulseInterpretaMatriz() {
        var fila = consultar(
            new AdaptadorGeoPulseMTR900(cliente, "/geo", "k"),
            103,
            "[[103,-31.42,-64.18,\"2026-11-15T11:00:00Z\"]]"
        );
        assertArrayEquals(
            new String[] { "103", "-31.42", "-64.18", "2026-11-15T11:00Z", null },
            fila
        );
    }

    @Test
    void cadaAdaptadorInformaFormatoInvalidoSinUbicacion() {
        IAdaptadorGPSTracker[] adaptadores = {
            new AdaptadorXTR4500L(cliente, "/xtr", "k"),
            new AdaptadorNavTrackQX7A(cliente, "/nav", "k"),
            new AdaptadorGeoPulseMTR900(cliente, "/geo", "k"),
        };
        for (var adaptador : adaptadores) {
            for (String respuesta : new String[] { "n/a", "{}", "[]" }) {
                var fila = consultar(adaptador, 101, respuesta);
                assertEquals("101", fila[0]);
                assertNull(fila[1]);
                assertNull(fila[2]);
                assertNull(fila[3]);
                assertNotNull(fila[4]);
            }
        }
    }

    @Test
    void cadaAdaptadorValidaCoordenadasYFechas() {
        IAdaptadorGPSTracker[] adaptadores = {
            new AdaptadorXTR4500L(cliente, "/xtr", "k"),
            new AdaptadorNavTrackQX7A(cliente, "/nav", "k"),
            new AdaptadorGeoPulseMTR900(cliente, "/geo", "k"),
        };
        String[][] respuestas = {
            {
                "{\"items\":[{\"numeroBolsin\":101,\"latitud\":91,\"longitud\":0,\"fechaHoraUltimaActualizacion\":\"2026-11-15T11:00Z\"}]}",
                "{\"items\":[{\"numeroBolsin\":101,\"latitud\":0,\"longitud\":0,\"fechaHoraUltimaActualizacion\":\"invalida\"}]}",
            },
            { "101,91,0,2026-11-15T11:00Z", "101,0,0,invalida" },
            { "[[101,91,0,\"2026-11-15T11:00Z\"]]", "[[101,0,0,\"invalida\"]]" },
        };
        for (int i = 0; i < adaptadores.length; i++) for (String respuesta : respuestas[i]) {
            var fila = consultar(adaptadores[i], 101, respuesta);
            assertNull(fila[1]);
            assertNull(fila[2]);
            assertNull(fila[3]);
            assertNotNull(fila[4]);
        }
    }
}
