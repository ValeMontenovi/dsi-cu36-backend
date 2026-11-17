package ar.edu.utn.dsi.bolsines.config;

import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.repository.*;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

/** Demo fixture matches original simulator InMemoryBolsinRepository. */
@Configuration
@Profile("demo")
public class DatosDemo {

    @Bean
    @Order(1)
    CommandLineRunner demo(ComisionMedicaRepository cm, EmpleadoRepository em, BolsinRepository b) {
        return args -> {
            ComisionMedica cordoba = cm
                .findById("CM-CBA-01")
                .orElseGet(() ->
                    cm.save(new ComisionMedica("CM-CBA-01", "Córdoba", "gerente-cba@example.test"))
                );
            ComisionMedica rosario = cm
                .findById("CM-ROS-02")
                .orElseGet(() ->
                    cm.save(new ComisionMedica("CM-ROS-02", "Rosario", "gerente-ros@example.test"))
                );
            ComisionMedica mendoza = cm
                .findById("CM-MDZ-03")
                .orElseGet(() ->
                    cm.save(new ComisionMedica("CM-MDZ-03", "Mendoza", "gerente-mdz@example.test"))
                );
            ComisionMedica buenosAires = cm
                .findById("CM-BA-01")
                .orElseGet(() ->
                    cm.save(
                        new ComisionMedica("CM-BA-01", "Buenos Aires", "gerente-ba@example.test")
                    )
                );
            em.findByUsuario("encargado-cba").orElseGet(() ->
                em.save(new Empleado("encargado-cba", cordoba))
            );
            em.findByUsuario("encargado-ros").orElseGet(() ->
                em.save(new Empleado("encargado-ros", rosario))
            );
            em.findByUsuario("encargado-mdz").orElseGet(() ->
                em.save(new Empleado("encargado-mdz", mendoza))
            );
            if (!b.existsById(101L)) b.save(
                new Bolsin(
                    101L,
                    "P-101",
                    cordoba,
                    rosario,
                    List.of(
                        new CambioEstadoBolsin(OffsetDateTime.now(), null, new Estado("ENVIADO"))
                    ),
                    ModeloGps.XTR_4500L
                )
            );
            if (!b.existsById(102L)) b.save(
                new Bolsin(
                    102L,
                    "P-102",
                    rosario,
                    mendoza,
                    List.of(
                        new CambioEstadoBolsin(OffsetDateTime.now(), null, new Estado("ENVIADO"))
                    ),
                    ModeloGps.NAVTRACK_QX_7A
                )
            );
            if (!b.existsById(103L)) b.save(
                new Bolsin(
                    103L,
                    "P-103",
                    mendoza,
                    buenosAires,
                    List.of(
                        new CambioEstadoBolsin(OffsetDateTime.now(), null, new Estado("ENVIADO"))
                    ),
                    ModeloGps.GEOPULSE_MTR_900
                )
            );
            if (!b.existsById(104L)) b.save(
                new Bolsin(
                    104L,
                    "P-104",
                    buenosAires,
                    cordoba,
                    List.of(
                        new CambioEstadoBolsin(OffsetDateTime.now(), null, new Estado("CERRADO"))
                    ),
                    ModeloGps.XTR_4500L
                )
            );
        };
    }
}
