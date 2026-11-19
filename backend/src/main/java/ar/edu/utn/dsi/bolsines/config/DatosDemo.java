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

/**
 * Datos demo basados en el InMemoryBolsinRepository del simulador de la cátedra (bolsines 101
 * a 110), con el 113 agregado para la prueba de integración local. Cada modelo de GPS valida
 * distinto: XTR exige la CM origen del simulador, NavTrack la
 * CM destino y GeoPulse solo el número.
 *
 * <p>Por encargado:
 *
 * <ul>
 *   <li>encargado-cba (Córdoba): 101, 105, 107 y 113 enviados.
 *   <li>encargado-ros (Rosario): 102 y 106 enviados.
 *   <li>encargado-mdz (Mendoza): 103 y 110 enviados.
 *   <li>encargado-ba (Buenos Aires): 108 y 109 enviados, 111 enviado sin GPS (el simulador no lo
 *       conoce: flujo alternativo A3) y 104 cerrado.
 *   <li>encargado-pna (Paraná): ningún bolsín enviado, solo 112 cerrado (flujo alternativo A1).
 * </ul>
 *
 * <p>Solo se crea lo que no existe, así que una base ya cargada conserva sus datos.
 */
@Configuration
@Profile("demo")
public class DatosDemo {

    @Bean
    @Order(1)
    CommandLineRunner demo(ComisionMedicaRepository cm, EmpleadoRepository em, BolsinRepository b) {
        return args -> {
            ComisionMedica cordoba = comision(cm, "CM-CBA-01", "Córdoba", "gerente-cba");
            ComisionMedica rosario = comision(cm, "CM-ROS-02", "Rosario", "gerente-ros");
            ComisionMedica mendoza = comision(cm, "CM-MDZ-03", "Mendoza", "gerente-mdz");
            ComisionMedica buenosAires = comision(cm, "CM-BA-01", "Buenos Aires", "gerente-ba");
            ComisionMedica tucuman = comision(cm, "CM-TUC-01", "Tucumán", "gerente-tuc");
            ComisionMedica salta = comision(cm, "CM-SLA-01", "Salta", "gerente-sla");
            ComisionMedica neuquen = comision(cm, "CM-NQN-01", "Neuquén", "gerente-nqn");
            ComisionMedica marDelPlata = comision(cm, "CM-MDQ-01", "Mar del Plata", "gerente-mdq");
            ComisionMedica parana = comision(cm, "CM-PNA-01", "Paraná", "gerente-pna");

            empleado(em, "encargado-cba", cordoba);
            empleado(em, "encargado-ros", rosario);
            empleado(em, "encargado-mdz", mendoza);
            empleado(em, "encargado-ba", buenosAires);
            empleado(em, "encargado-pna", parana);

            bolsin(b, 101L, cordoba, rosario, "ENVIADO", ModeloGps.XTR_4500L);
            bolsin(b, 102L, rosario, mendoza, "ENVIADO", ModeloGps.NAVTRACK_QX_7A);
            bolsin(b, 103L, mendoza, buenosAires, "ENVIADO", ModeloGps.GEOPULSE_MTR_900);
            bolsin(b, 104L, buenosAires, cordoba, "CERRADO", ModeloGps.XTR_4500L);
            bolsin(b, 105L, cordoba, tucuman, "ENVIADO", ModeloGps.GEOPULSE_MTR_900);
            bolsin(b, 106L, rosario, salta, "ENVIADO", ModeloGps.GEOPULSE_MTR_900);
            bolsin(b, 107L, cordoba, neuquen, "ENVIADO", ModeloGps.GEOPULSE_MTR_900);
            bolsin(b, 108L, buenosAires, marDelPlata, "ENVIADO", ModeloGps.GEOPULSE_MTR_900);
            bolsin(b, 109L, buenosAires, cordoba, "ENVIADO", ModeloGps.NAVTRACK_QX_7A);
            bolsin(b, 110L, mendoza, rosario, "ENVIADO", ModeloGps.NAVTRACK_QX_7A);
            bolsin(b, 111L, buenosAires, mendoza, "ENVIADO", ModeloGps.XTR_4500L);
            bolsin(b, 112L, parana, buenosAires, "CERRADO", ModeloGps.XTR_4500L);
            bolsin(b, 113L, cordoba, rosario, "ENVIADO", ModeloGps.XTR_4500L);
            bolsin(b, 115L, cordoba, rosario, "ENVIADO", ModeloGps.XTR_4500L);
        };
    }

    private static ComisionMedica comision(
        ComisionMedicaRepository cm,
        String codigo,
        String nombre,
        String gerente
    ) {
        return cm
            .findById(codigo)
            .orElseGet(() ->
                cm.save(new ComisionMedica(codigo, nombre, gerente + "@example.test"))
            );
    }

    private static void empleado(EmpleadoRepository em, String usuario, ComisionMedica cm) {
        em.findByUsuario(usuario).orElseGet(() -> em.save(new Empleado(usuario, cm)));
    }

    private static void bolsin(
        BolsinRepository b,
        Long numero,
        ComisionMedica origen,
        ComisionMedica destino,
        String estado,
        ModeloGps modelo
    ) {
        if (b.existsById(numero)) return;
        b.save(
            new Bolsin(
                numero,
                "P-" + numero,
                origen,
                destino,
                List.of(new CambioEstadoBolsin(OffsetDateTime.now(), null, new Estado(estado))),
                modelo
            )
        );
    }
}
