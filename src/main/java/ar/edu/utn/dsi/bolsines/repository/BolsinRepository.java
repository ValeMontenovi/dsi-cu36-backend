package ar.edu.utn.dsi.bolsines.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.utn.dsi.bolsines.domain.*;
public interface BolsinRepository extends JpaRepository<Bolsin,Long> {
 List<Bolsin> findByOrigenCodigoAndEstado(String codigo,EstadoBolsin estado);
 List<Bolsin> findByOrigenCodigoAndEstadoAndNumeroPrecinto(String codigo,EstadoBolsin estado,String precinto);
 List<Bolsin> findByOrigenCodigoAndEstadoAndDestinoCodigo(String codigo,EstadoBolsin estado,String codigoDestino);
}
