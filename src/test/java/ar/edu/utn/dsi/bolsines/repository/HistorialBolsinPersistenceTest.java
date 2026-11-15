package ar.edu.utn.dsi.bolsines.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.time.OffsetDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ar.edu.utn.dsi.bolsines.config.MigracionEstadoBolsin;
import ar.edu.utn.dsi.bolsines.domain.*;

@DataJpaTest
class HistorialBolsinPersistenceTest {
    @Autowired EntityManager em;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager tm;
    @Autowired BolsinRepository bolsines;

    @Test void conservaLegadoMigraUnaVezYPersisteNuevosHistoriales() {
        var cm=new ComisionMedica("CBA","Cordoba","mail");
        em.persist(cm); em.flush();
        jdbc.execute("ALTER TABLE bolsin ADD COLUMN estado VARCHAR(255)");
        jdbc.update("""
            INSERT INTO bolsin (numero, numero_precinto, origen_codigo, destino_codigo, modelo_gps, estado)
            VALUES (101, 'P-101', 'CBA', 'CBA', 'XTR_4500L', 'ENVIADO')
            """);
        jdbc.execute("ALTER TABLE bolsin ALTER COLUMN estado SET NOT NULL");
        var migracion=new MigracionEstadoBolsin(jdbc,new TransactionTemplate(tm));
        migracion.run();
        em.clear();
        var importado=bolsines.findById(101L).orElseThrow();
        assertTrue(importado.sosEnviado());
        var cambio=importado.getCambioEstado().get(0);
        assertTrue(cambio.isImportado());
        assertNull(cambio.getFechaHoraInicio());
        assertNull(cambio.getFechaHoraFin());
        assertEquals("ENVIADO",jdbc.queryForObject("SELECT estado FROM bolsin WHERE numero=101",String.class));
        migracion.run();
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM cambio_estado_bolsin",Integer.class));

        var estado=em.find(Estado.class,"ENVIADO");
        var actual=new CambioEstadoBolsin(OffsetDateTime.parse("2026-11-15T10:00:00Z"),null,estado);
        bolsines.saveAndFlush(new Bolsin(102L,"P-102",em.find(ComisionMedica.class,"CBA"),
            em.find(ComisionMedica.class,"CBA"),List.of(actual),ModeloGps.XTR_4500L));
        em.clear();
        assertTrue(bolsines.findById(102L).orElseThrow().sosEnviado());
        // Una inconsistencia posterior no se repara silenciosamente volviendo a importar.
        jdbc.update("DELETE FROM cambio_estado_bolsin WHERE bolsin_numero=101");
        migracion.run(); em.clear();
        assertThrows(InconsistenciaEstadoBolsinException.class,()->bolsines.findById(101L).orElseThrow().sosEnviado());
    }
}
