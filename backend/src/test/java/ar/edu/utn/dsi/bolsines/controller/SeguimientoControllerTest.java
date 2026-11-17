package ar.edu.utn.dsi.bolsines.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.dsi.bolsines.domain.Sesion;
import ar.edu.utn.dsi.bolsines.service.GestorSeguimientoBolsin;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SeguimientoControllerTest {

    @Test
    void cadaPeticionEntregaSuPropiaSesionAlGestor() throws Exception {
        var gestor = mock(GestorSeguimientoBolsin.class);
        var mvc = MockMvcBuilders.standaloneSetup(new SeguimientoController(gestor)).build();

        mvc.perform(get("/api/seguimiento").param("usuario", "encargado-cba")).andExpect(
            status().isOk()
        );
        mvc.perform(get("/api/seguimiento").param("usuario", "encargado-ros")).andExpect(
            status().isOk()
        );

        var sesiones = ArgumentCaptor.forClass(Sesion.class);
        verify(gestor, times(2)).consultar(sesiones.capture(), isNull(), isNull());
        var primera = sesiones.getAllValues().get(0);
        var segunda = sesiones.getAllValues().get(1);
        assertNotSame(primera, segunda);
        assertEquals("encargado-cba", primera.getUsuario());
        assertEquals("encargado-ros", segunda.getUsuario());
    }

    @Test
    void notificarConservaEndpointYRecibeSesionDeLaPeticion() throws Exception {
        var gestor = mock(GestorSeguimientoBolsin.class);
        var mvc = MockMvcBuilders.standaloneSetup(new SeguimientoController(gestor)).build();
        mvc.perform(
            post("/api/seguimiento/101/notificar").param("usuario", "encargado-cba")
        ).andExpect(status().isNoContent());
        var sesion = ArgumentCaptor.forClass(Sesion.class);
        verify(gestor).notificar(sesion.capture(), eq(101L));
        assertEquals("encargado-cba", sesion.getValue().getUsuario());
    }

    @Test
    void informaInconsistenciaComoConflicto() throws Exception {
        var gestor = mock(GestorSeguimientoBolsin.class);
        when(gestor.consultar(any(Sesion.class), isNull(), isNull())).thenThrow(
            new ar.edu.utn.dsi.bolsines.domain.InconsistenciaEstadoBolsinException(101L, 2)
        );
        var mvc = MockMvcBuilders.standaloneSetup(new SeguimientoController(gestor)).build();
        mvc.perform(get("/api/seguimiento").param("usuario", "encargado-cba"))
            .andExpect(status().isConflict())
            .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(
                    org.hamcrest.Matchers.containsString("encontrados 2")
                )
            );
    }
}
