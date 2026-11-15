package ar.edu.utn.dsi.bolsines.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.utn.dsi.bolsines.repository.BolsinRepository;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:demo_historial;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.h2.console.enabled=false"})
@ActiveProfiles("demo")
@Transactional
class DatosDemoHistorialTest {
    @Autowired BolsinRepository bolsines;
    @Test void inicioCompletoCreaDatosDemoConUnCambioActual() {
        for(long numero : new long[]{101,102,103,104}) {
            var bolsin=bolsines.findById(numero).orElseThrow();
            assertEquals(1,bolsin.getCambioEstado().size());
            var cambio=bolsin.getCambioEstado().get(0);
            assertTrue(cambio.sosActual());
            assertNotNull(cambio.getFechaHoraInicio());
            assertFalse(cambio.isImportado());
            assertEquals(numero!=104,bolsin.sosEnviado());
        }
    }
}
