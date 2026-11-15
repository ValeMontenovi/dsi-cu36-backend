package ar.edu.utn.dsi.bolsines.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.aryEq;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ar.edu.utn.dsi.bolsines.adapter.*;
import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.repository.*;

class GestorSeguimientoBolsinTest {
  private final BolsinRepository bolsines = mock(BolsinRepository.class);
  private final EmpleadoRepository empleados = mock(EmpleadoRepository.class);
  private final InterfazGPSTracker selector = mock(InterfazGPSTracker.class);
  private final NotificadorUbicacion notificador = mock(NotificadorUbicacion.class);
  private final GestorSeguimientoBolsin gestor = new GestorSeguimientoBolsin(bolsines, empleados, selector, notificador);
  private final ComisionMedica origen = new ComisionMedica("CBA", "Córdoba", "origen@example.test");
  private final ComisionMedica destino = new ComisionMedica("ROS", "Rosario", "destino@example.test");

  @Test void noConsultaBolsinesDeOtrasComisiones() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findAll()).thenReturn(List.of());
    assertTrue(gestor.consultar(new Sesion("encargado-cba"), null, null).isEmpty());
    verify(bolsines).findAll();
    verifyNoInteractions(selector);
  }

  @Test void filtraPorPrecintoAntesDeLlamarGps() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findAll()).thenReturn(
       List.of(new Bolsin(101L, "P-101", origen, destino, List.of(new CambioEstadoBolsin(java.time.OffsetDateTime.parse("2026-11-15T10:00:00Z"),null,new Estado("ENVIADO"))), ModeloGps.XTR_4500L)));
    assertTrue(gestor.consultar(new Sesion("encargado-cba"), "NO-EXISTE", null).isEmpty());
    verifyNoInteractions(selector);
  }

  @Test void impideNotificarBolsinNoEnviado() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findById(103L)).thenReturn(Optional.of(
       new Bolsin(103L, "P-103", origen, destino, List.of(new CambioEstadoBolsin(java.time.OffsetDateTime.parse("2026-11-15T10:00:00Z"),null,new Estado("CERRADO"))), ModeloGps.GEOPULSE_MTR_900)));
    assertThrows(IllegalArgumentException.class, () -> gestor.notificar(new Sesion("encargado-cba"), 103L));
    verifyNoInteractions(notificador);
  }

  @Test void identificaEmpleadoMedianteLosMensajesDeLaSecuencia() {
    Sesion sesion = mock(Sesion.class);
    Empleado otro = mock(Empleado.class);
    Empleado identificado = mock(Empleado.class);
    when(sesion.getUsuario()).thenReturn("encargado-cba");
    when(empleados.findAll()).thenReturn(List.of(otro, identificado));
    when(identificado.esTuUsuario("encargado-cba")).thenReturn(true);
    when(identificado.obtenerCM()).thenReturn("CBA");
    when(bolsines.findAll()).thenReturn(List.of());

    gestor.consultar(sesion, null, null);

    var orden = inOrder(sesion, otro, identificado, bolsines);
    orden.verify(sesion).getUsuario();
    orden.verify(otro).esTuUsuario("encargado-cba");
    orden.verify(identificado).esTuUsuario("encargado-cba");
    orden.verify(identificado).obtenerCM();
    orden.verify(bolsines).findAll();
    verify(otro, never()).obtenerCM();
    verify(empleados, never()).findByUsuario(anyString());
  }

  @Test void rechazaUsuarioSinEmpleadoAntesDeConsultarBolsines() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("otro", origen)));
    var error = assertThrows(IllegalArgumentException.class,
        () -> gestor.consultar(new Sesion("inexistente"), null, null));
    assertEquals("Usuario no encontrado: inexistente", error.getMessage());
    verifyNoInteractions(bolsines, selector, notificador);
  }

  @Test void empleadoComparaUsuarioYDevuelveCodigoDeSuCM() {
    Empleado empleado = new Empleado("encargado-cba", origen);
    assertTrue(empleado.esTuUsuario("encargado-cba"));
    assertFalse(empleado.esTuUsuario("otro"));
    assertFalse(empleado.esTuUsuario(null));
    assertEquals("CBA", empleado.obtenerCM());
  }

  @Test void consultaOrigenYEstadoSoloDeLaCMDelEmpleado() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba", origen)));
    Bolsin ajeno=mock(Bolsin.class);
    Bolsin cerrado=mock(Bolsin.class);
    when(cerrado.esTuCMOrigen("CBA")).thenReturn(true);
    when(cerrado.sosEnviado()).thenReturn(false);
    when(bolsines.findAll()).thenReturn(List.of(ajeno,cerrado));
    assertTrue(gestor.consultar(new Sesion("encargado-cba"),null,null).isEmpty());
    verify(ajeno).esTuCMOrigen("CBA");
    verify(ajeno,never()).sosEnviado();
    var orden=inOrder(cerrado);
    orden.verify(cerrado).esTuCMOrigen("CBA");
    orden.verify(cerrado).sosEnviado();
    verifyNoInteractions(selector);
  }

  @Test void propagaInconsistenciaAntesDeLocalizar() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba", origen)));
    when(bolsines.findAll()).thenReturn(List.of(new Bolsin(101L,"P-101",origen,destino,List.of(),ModeloGps.XTR_4500L)));
    assertThrows(InconsistenciaEstadoBolsinException.class,
        ()->gestor.consultar(new Sesion("encargado-cba"),null,null));
    verifyNoInteractions(selector);
  }

  private Bolsin enviado(long numero,ModeloGps modelo) {
    return new Bolsin(numero,"P-"+numero,origen,destino,List.of(new CambioEstadoBolsin(
        java.time.OffsetDateTime.parse("2026-11-15T10:00:00Z"),null,new Estado("ENVIADO"))),modelo);
  }
  @Test void armaArreglosParalelosYConvierteSoloElContratoNormalizado() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba",origen)));
    when(bolsines.findAll()).thenReturn(List.of(enviado(103,ModeloGps.GEOPULSE_MTR_900),enviado(101,ModeloGps.XTR_4500L)));
    when(selector.obtenerLocalizacion(aryEq(new int[]{103,101}),aryEq(new String[]{"GEOPULSE_MTR_900","XTR_4500L"}),eq("CBA"),aryEq(new String[]{"ROS","ROS"})))
      .thenReturn(new String[][]{{"103","0.0","0.0","2026-11-15T10:00Z",null},{"101",null,null,null,"GPS no disponible"}});
    var resultado=gestor.consultar(new Sesion("encargado-cba"),null,null);
    assertEquals(103L,resultado.get(0).numeroBolsin());
    assertEquals(0.0,resultado.get(0).ubicacion().latitud());
    assertNull(resultado.get(0).errorGps());
    assertEquals(101L,resultado.get(1).numeroBolsin());
    assertNull(resultado.get(1).ubicacion());
    assertEquals("GPS no disponible",resultado.get(1).errorGps());
  }
  @Test void rechazaLongFueraDeRangoAntesDeLlamarBoundary() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba",origen)));
    when(bolsines.findAll()).thenReturn(List.of(enviado((long)Integer.MAX_VALUE+1,ModeloGps.XTR_4500L)));
    assertThrows(IllegalArgumentException.class,()->gestor.consultar(new Sesion("encargado-cba"),null,null));
    verifyNoInteractions(selector);
  }
  @Test void compatibilidadNotificacionNoAceptaUbicacionFallida() {
    when(empleados.findAll()).thenReturn(List.of(new Empleado("encargado-cba",origen)));
    when(bolsines.findById(101L)).thenReturn(Optional.of(enviado(101,ModeloGps.XTR_4500L)));
    when(selector.obtenerLocalizacion(any(),any(),anyString(),any())).thenReturn(new String[][]{{"101",null,null,null,"Respuesta de otro bolsin"}});
    assertThrows(GpsException.class,()->gestor.notificar(new Sesion("encargado-cba"),101L));
    verifyNoInteractions(notificador);
  }
}
