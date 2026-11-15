package ar.edu.utn.dsi.bolsines.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.utn.dsi.bolsines.domain.Bolsin;
public interface BolsinRepository extends JpaRepository<Bolsin,Long> {}
