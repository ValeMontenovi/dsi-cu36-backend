package ar.edu.utn.dsi.bolsines.config;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.dsi.bolsines.repository.BolsinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
        "spring.datasource.url=jdbc:h2:mem:demo_historial;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false",
    }
)
@ActiveProfiles("demo")
@Transactional
class DatosDemoHistorialTest {

    @Autowired
    BolsinRepository bolsines;

    @Test
    void inicioCompletoCreaDatosDemoConUnCambioActual() {
        for (long numero : new long[] { 101, 102, 103, 104 }) {
            var bolsin = bolsines.findById(numero).orElseThrow();
            assertEquals(1, bolsin.getCambioEstado().size());
            var cambio = bolsin.getCambioEstado().get(0);
            assertTrue(cambio.sosActual());
            assertNotNull(cambio.getFechaHoraInicio());
            assertFalse(cambio.isImportado());
            assertEquals(numero != 104, bolsin.sosEnviado());
        }
    }

    @Test
    void bolsinesNuevosCubrenBuenosAiresYLosFlujosAlternativos() {
        for (long numero = 105; numero <= 111; numero++) {
            assertTrue(bolsines.findById(numero).orElseThrow().sosEnviado(), "bolsín " + numero);
        }
        // 111 no existe en el simulador: el GPS no puede ubicarlo (A3)
        assertTrue(bolsines.findById(111L).orElseThrow().esTuCMOrigen("CM-BA-01"));
        // Paraná no tiene ningún bolsín enviado (A1)
        var parana = bolsines
            .findAll()
            .stream()
            .filter(b -> b.esTuCMOrigen("CM-PNA-01"))
            .toList();
        assertEquals(1, parana.size());
        assertFalse(parana.get(0).sosEnviado());
    }
}
