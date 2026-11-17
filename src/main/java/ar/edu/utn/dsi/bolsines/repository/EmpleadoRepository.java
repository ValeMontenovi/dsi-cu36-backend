package ar.edu.utn.dsi.bolsines.repository;

import ar.edu.utn.dsi.bolsines.domain.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByUsuario(String usuario);
}
