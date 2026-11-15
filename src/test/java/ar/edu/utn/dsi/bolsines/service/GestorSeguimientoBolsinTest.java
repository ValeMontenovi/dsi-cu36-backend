package ar.edu.utn.dsi.bolsines.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ar.edu.utn.dsi.bolsines.adapter.*;
import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.repository.*;

class GestorSeguimientoBolsinTest {
  private final BolsinRepository bolsines = mock(BolsinRepository.class);
  private final EmpleadoRepository empleados = mock(EmpleadoRepository.class);
  private final SelectorGpsTracker selector = mock(SelectorGpsTracker.class);
  private final NotificadorUbicacion notificador = mock(NotificadorUbicacion.class);
  private final GestorSeguimientoBolsin gestor = new GestorSeguimientoBolsin(bolsines, empleados, selector, notificador);
  private final ComisionMedica origen = new ComisionMedica("CBA", "Córdoba", "origen@example.test");
  private final ComisionMedica destino = new ComisionMedica("ROS", "Rosario", "destino@example.test");

  @Test void noConsultaBolsinesDeOtrasComisiones() {
    when(empleados.findByUsuario("encargado-cba")).thenReturn(Optional.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findByOrigenCodigoAndEstado("CBA", EstadoBolsin.ENVIADO)).thenReturn(List.of());
    assertTrue(gestor.consultar("encargado-cba", null, null).isEmpty());
    verify(bolsines).findByOrigenCodigoAndEstado("CBA", EstadoBolsin.ENVIADO);
    verifyNoInteractions(selector);
  }

  @Test void filtraPorPrecintoAntesDeLlamarGps() {
    when(empleados.findByUsuario("encargado-cba")).thenReturn(Optional.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findByOrigenCodigoAndEstado("CBA", EstadoBolsin.ENVIADO)).thenReturn(
       List.of(new Bolsin(101L, "P-101", origen, destino, EstadoBolsin.ENVIADO, ModeloGps.XTR_4500L)));
    assertTrue(gestor.consultar("encargado-cba", "NO-EXISTE", null).isEmpty());
    verifyNoInteractions(selector);
  }

  @Test void impideNotificarBolsinNoEnviado() {
    when(empleados.findByUsuario("encargado-cba")).thenReturn(Optional.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findById(103L)).thenReturn(Optional.of(
       new Bolsin(103L, "P-103", origen, destino, EstadoBolsin.CERRADO, ModeloGps.GEOPULSE_MTR_900)));
    assertThrows(IllegalArgumentException.class, () -> gestor.notificar("encargado-cba", 103L));
    verifyNoInteractions(notificador);
  }
}
