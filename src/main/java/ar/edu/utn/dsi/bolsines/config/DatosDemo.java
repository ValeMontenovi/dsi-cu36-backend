package ar.edu.utn.dsi.bolsines.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.repository.*;

/** Demo fixture matches original simulator InMemoryBolsinRepository. */
@Configuration @Profile("demo") public class DatosDemo {
 @Bean CommandLineRunner demo(ComisionMedicaRepository cm,EmpleadoRepository em,BolsinRepository b){return args->{
 ComisionMedica cordoba=cm.findById("CM-CBA-01").orElseGet(()->cm.save(new ComisionMedica("CM-CBA-01","Córdoba","gerente-cba@example.test")));
 ComisionMedica rosario=cm.findById("CM-ROS-02").orElseGet(()->cm.save(new ComisionMedica("CM-ROS-02","Rosario","gerente-ros@example.test")));
 ComisionMedica mendoza=cm.findById("CM-MDZ-03").orElseGet(()->cm.save(new ComisionMedica("CM-MDZ-03","Mendoza","gerente-mdz@example.test")));
 ComisionMedica buenosAires=cm.findById("CM-BA-01").orElseGet(()->cm.save(new ComisionMedica("CM-BA-01","Buenos Aires","gerente-ba@example.test")));
 em.findByUsuario("encargado-cba").orElseGet(()->em.save(new Empleado("encargado-cba",cordoba)));
 em.findByUsuario("encargado-ros").orElseGet(()->em.save(new Empleado("encargado-ros",rosario)));
 em.findByUsuario("encargado-mdz").orElseGet(()->em.save(new Empleado("encargado-mdz",mendoza)));
 if(!b.existsById(101L))b.save(new Bolsin(101L,"P-101",cordoba,rosario,EstadoBolsin.ENVIADO,ModeloGps.XTR_4500L));
 if(!b.existsById(102L))b.save(new Bolsin(102L,"P-102",rosario,mendoza,EstadoBolsin.ENVIADO,ModeloGps.NAVTRACK_QX_7A));
 if(!b.existsById(103L))b.save(new Bolsin(103L,"P-103",mendoza,buenosAires,EstadoBolsin.ENVIADO,ModeloGps.GEOPULSE_MTR_900));
 if(!b.existsById(104L))b.save(new Bolsin(104L,"P-104",buenosAires,cordoba,EstadoBolsin.CERRADO,ModeloGps.XTR_4500L));
 };}
}
