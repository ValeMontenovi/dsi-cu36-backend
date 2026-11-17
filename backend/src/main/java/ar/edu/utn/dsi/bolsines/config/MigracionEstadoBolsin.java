package ar.edu.utn.dsi.bolsines.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/** Importacion unica del estado legado; no reconstruye fechas historicas. */
@Component
@Order(0)
public class MigracionEstadoBolsin implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    public MigracionEstadoBolsin(JdbcTemplate jdbc, TransactionTemplate transaction) {
        this.jdbc = jdbc;
        this.transaction = transaction;
    }

    @Override
    public void run(String... args) {
        // Hibernate update crea las tablas nuevas antes de ejecutar este runner.
        Integer columnas = jdbc.queryForObject(
            """
            SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME='BOLSIN' AND COLUMN_NAME='ESTADO'
            """,
            Integer.class
        );
        if (columnas == 0) return;
        // La columna legada queda archivada, sin participar de lecturas o escrituras del dominio.
        jdbc.execute("ALTER TABLE bolsin ALTER COLUMN estado DROP NOT NULL");
        jdbc.execute(
            "CREATE TABLE IF NOT EXISTS migracion_estado_bolsin (version INTEGER PRIMARY KEY)"
        );
        transaction.executeWithoutResult(status -> {
            if (
                jdbc.queryForObject(
                    "SELECT COUNT(*) FROM migracion_estado_bolsin WHERE version=1",
                    Integer.class
                ) > 0
            ) return;
            jdbc.update(
                """
                INSERT INTO estado (nombre, ambito, descripcion)
                SELECT DISTINCT b.estado, 'BOLSIN', 'Estado conservado del modelo anterior'
                FROM bolsin b WHERE b.estado IS NOT NULL
                AND NOT EXISTS (SELECT 1 FROM estado e WHERE e.nombre=b.estado)
                """
            );
            jdbc.update(
                """
                INSERT INTO cambio_estado_bolsin
                    (bolsin_numero, estado_nombre, fecha_hora_inicio, fecha_hora_fin, importado)
                SELECT b.numero, b.estado, NULL, NULL, TRUE FROM bolsin b
                WHERE b.estado IS NOT NULL
                AND NOT EXISTS (SELECT 1 FROM cambio_estado_bolsin c WHERE c.bolsin_numero=b.numero)
                """
            );
            jdbc.update("INSERT INTO migracion_estado_bolsin (version) VALUES (1)");
        });
    }
}
