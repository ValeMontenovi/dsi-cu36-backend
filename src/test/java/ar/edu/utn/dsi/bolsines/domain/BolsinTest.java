package ar.edu.utn.dsi.bolsines.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class BolsinTest {
    private final OffsetDateTime fecha=OffsetDateTime.parse("2026-11-15T10:00:00Z");
    private Bolsin bolsin(List<CambioEstadoBolsin> cambios) {
        var cm=new ComisionMedica("CBA","Cordoba","mail");
        return new Bolsin(101L,"P-101",cm,cm,cambios,ModeloGps.XTR_4500L);
    }
    @Test void usaCambioAbiertoAunqueUnCerradoTengaFechaPosterior() {
        var abierto=new CambioEstadoBolsin(fecha,null,new Estado("CERRADO"));
        var cerrado=new CambioEstadoBolsin(fecha.plusDays(1),fecha.plusDays(2),new Estado("ENVIADO"));
        assertTrue(abierto.sosActual());
        assertFalse(cerrado.sosActual());
        assertFalse(bolsin(List.of(cerrado,abierto)).sosEnviado());
    }
    @Test void informaCeroActualesInclusoConHistorialCerrado() {
        assertThrows(InconsistenciaEstadoBolsinException.class,()->bolsin(List.of()).sosEnviado());
        var cerrado=new CambioEstadoBolsin(fecha,fecha.plusDays(1),new Estado("ENVIADO"));
        var error=assertThrows(InconsistenciaEstadoBolsinException.class,()->bolsin(List.of(cerrado)).sosEnviado());
        assertTrue(error.getMessage().contains("101"));
        assertTrue(error.getMessage().contains("encontrados 0"));
    }
    @Test void informaMultiplesActuales() {
        var a=new CambioEstadoBolsin(fecha,null,new Estado("ENVIADO"));
        var b=new CambioEstadoBolsin(fecha.plusDays(1),null,new Estado("CERRADO"));
        var error=assertThrows(InconsistenciaEstadoBolsinException.class,()->bolsin(List.of(a,b)).sosEnviado());
        assertTrue(error.getMessage().contains("encontrados 2"));
    }
    @Test void delegaDelBolsinAlCambioYDelCambioAlEstado() {
        var estado=mock(Estado.class);
        when(estado.esEnviado()).thenReturn(true);
        var cambio=spy(new CambioEstadoBolsin(fecha,null,estado));
        assertTrue(bolsin(List.of(cambio)).sosEnviado());
        var orden=inOrder(cambio,estado);
        orden.verify(cambio).sosActual();
        orden.verify(cambio).sosEnviado();
        orden.verify(estado).esEnviado();
    }
    @Test void comparaCodigoDeOrigen() {
        assertTrue(bolsin(List.of()).esTuCMOrigen("CBA"));
        assertFalse(bolsin(List.of()).esTuCMOrigen("ROS"));
    }
}
