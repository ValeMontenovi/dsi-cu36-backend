package ar.edu.utn.dsi.bolsines.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.dsi.bolsines.domain.ModeloGps;
import java.util.Map;
import org.junit.jupiter.api.Test;

class InterfazGPSTrackerTest {

    private final ClienteGpsHttp cliente = mock(ClienteGpsHttp.class);
    private final InterfazGPSTracker boundary = spy(
        new InterfazGPSTracker(cliente, "/xtr", "kx", "/nav", "kn", "/geo", "kg")
    );
    private static final String FECHA = "2026-03-07T12:30:00";

    private String xtr(int n, String lat) {
        return (
            "{\"items\":[{\"numeroBolsin\":" +
            n +
            ",\"latitud\":" +
            lat +
            ",\"longitud\":-64.1,\"fechaHoraUltimaActualizacion\":\"" +
            FECHA +
            "\"}]}"
        );
    }

    @Test
    void mezclaModelosConservaOrdenYReutilizaAdaptadorSoloEnEstaConsulta() {
        when(cliente.get(eq("/xtr"), eq("kx"), anyMap())).thenReturn(
            xtr(101, "-31.4"),
            xtr(104, "0"),
            xtr(101, "-31.4")
        );
        when(cliente.get(eq("/nav"), eq("kn"), anyMap())).thenReturn("102,-32.9,-60.6," + FECHA);
        when(cliente.get(eq("/geo"), eq("kg"), anyMap())).thenReturn(
            "[[103,-33,-61,\"" + FECHA + "\"]]"
        );
        var filas = boundary.obtenerLocalizacion(
            new int[] { 101, 102, 103, 104 },
            new String[] { "XTR_4500L", "NAVTRACK_QX_7A", "GEOPULSE_MTR_900", "XTR_4500L" },
            "ORIGEN",
            new String[] { "D1", "D2", "D3", "D4" }
        );
        for (int i = 0; i < 4; i++) {
            assertEquals(Integer.toString(101 + i), filas[i][0]);
            assertEquals(5, filas[i].length);
            assertNull(filas[i][4]);
            assertNotNull(filas[i][1]);
            assertNotNull(filas[i][2]);
            assertEquals("2026-03-07T12:30Z", filas[i][3]);
        }
        assertEquals("0.0", filas[3][1]); // Cero real es una coordenada valida.
        for (var modelo : ModeloGps.values()) verify(boundary).crearAdaptador(modelo);
        verify(cliente).get(
            "/xtr",
            "kx",
            Map.of("numeroBolsin", "101", "codigoComisionMedicaOrigen", "ORIGEN")
        );
        verify(cliente).get(
            "/nav",
            "kn",
            Map.of("numeroBolsin", "102", "codigoComisionMedicaDestino", "D2")
        );
        verify(cliente).get("/geo", "kg", Map.of("numeroBolsin", "103"));
        boundary.obtenerLocalizacion(
            new int[] { 101 },
            new String[] { "XTR_4500L" },
            "ORIGEN",
            new String[] { "D1" }
        );
        verify(boundary, times(2)).crearAdaptador(ModeloGps.XTR_4500L);
    }

    @Test
    void conservaFallosParcialesSinCoordenadasInventadas() {
        when(cliente.get(eq("/xtr"), anyString(), anyMap())).thenThrow(
            new GpsException("GPS no disponible")
        );
        when(cliente.get(eq("/nav"), anyString(), anyMap())).thenReturn("999,-32,-60," + FECHA);
        when(cliente.get(eq("/geo"), anyString(), anyMap())).thenReturn(
            "[[103,0,0,\"" + FECHA + "\"]]"
        );
        var filas = boundary.obtenerLocalizacion(
            new int[] { 101, 102, 103 },
            new String[] { "XTR_4500L", "NAVTRACK_QX_7A", "GEOPULSE_MTR_900" },
            "O",
            new String[] { "A", "B", "C" }
        );
        for (int i = 0; i < 2; i++) {
            assertEquals(Integer.toString(101 + i), filas[i][0]);
            assertNull(filas[i][1]);
            assertNull(filas[i][2]);
            assertNull(filas[i][3]);
            assertNotNull(filas[i][4]);
        }
        assertNull(filas[2][4]);
        assertEquals("0.0", filas[2][1]);
    }

    @Test
    void validaLosTresArreglosAntesDeConsultar() {
        assertThrows(IllegalArgumentException.class, () ->
            boundary.obtenerLocalizacion(null, new String[0], "O", new String[0])
        );
        assertThrows(IllegalArgumentException.class, () ->
            boundary.obtenerLocalizacion(new int[1], new String[0], "O", new String[1])
        );
        assertThrows(IllegalArgumentException.class, () ->
            boundary.obtenerLocalizacion(new int[1], new String[1], "O", new String[0])
        );
        assertThrows(IllegalArgumentException.class, () ->
            boundary.obtenerLocalizacion(
                new int[] { 1, 2 },
                new String[] { "XTR_4500L", "otro" },
                "O",
                new String[2]
            )
        );
        assertThrows(IllegalArgumentException.class, () ->
            boundary.obtenerLocalizacion(new int[1], new String[1], "O", new String[1])
        );
        verifyNoInteractions(cliente);
    }

    @Test
    void entradaVaciaNoCreaAdaptadores() {
        assertEquals(
            0,
            boundary.obtenerLocalizacion(new int[0], new String[0], "O", new String[0]).length
        );
        verify(boundary, never()).crearAdaptador(any());
        verifyNoInteractions(cliente);
    }

    @Test
    void cadaAdaptadorRechazaNumeroIncorrectoYContinuaSuLote() {
        IGpsTracker[] adaptadores = {
            new AdaptadorXTR4500L(cliente, "/xtr", "k"),
            new AdaptadorNavTrackQX7A(cliente, "/nav", "k"),
            new AdaptadorGeoPulseMTR900(cliente, "/geo", "k"),
        };
        when(cliente.get(eq("/xtr"), anyString(), anyMap())).thenReturn(
            xtr(999, "1"),
            xtr(102, "1")
        );
        when(cliente.get(eq("/nav"), anyString(), anyMap())).thenReturn(
            "999,1,2," + FECHA,
            "102,1,2," + FECHA
        );
        when(cliente.get(eq("/geo"), anyString(), anyMap())).thenReturn(
            "[[999,1,2,\"" + FECHA + "\"]]",
            "[[102,1,2,\"" + FECHA + "\"]]"
        );
        for (var a : adaptadores) {
            var filas = a.recibirDatosLocalizacion(new int[] { 101, 102 }, "O", new String[] {
                "D1",
                "D2",
            });
            assertEquals("101", filas[0][0]);
            assertNull(filas[0][1]);
            assertNotNull(filas[0][4]);
            assertEquals("102", filas[1][0]);
            assertNull(filas[1][4]);
            assertThrows(IllegalArgumentException.class, () ->
                a.recibirDatosLocalizacion(new int[1], "O", new String[0])
            );
        }
    }

    @Test
    void camposAusentesYDestinoFaltanteSonErroresPorBolsin() {
        when(cliente.get(eq("/xtr"), anyString(), anyMap())).thenReturn(xtr(101, "null"));
        when(cliente.get(eq("/geo"), anyString(), anyMap())).thenReturn(
            "[[103,\"invalido\",2,\"" + FECHA + "\"]]"
        );
        var filas = boundary.obtenerLocalizacion(
            new int[] { 101, 102, 103 },
            new String[] { "XTR_4500L", "NAVTRACK_QX_7A", "GEOPULSE_MTR_900" },
            "O",
            new String[] { "D", null, "D" }
        );
        for (var fila : filas) {
            assertNull(fila[1]);
            assertNull(fila[2]);
            assertNull(fila[3]);
            assertNotNull(fila[4]);
        }
        verify(cliente, never()).get(eq("/nav"), anyString(), anyMap());
    }
}
