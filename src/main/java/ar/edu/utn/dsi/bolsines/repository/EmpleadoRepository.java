package ar.edu.utn.dsi.bolsines.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.utn.dsi.bolsines.domain.Empleado;
public interface EmpleadoRepository extends JpaRepository<Empleado,Long>{Optional<Empleado> findByUsuario(String usuario);}
