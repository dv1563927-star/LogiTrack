package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.Auditoria;
import CampusLands.logitrack.model.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Requisito 6: auditorias por usuario o por tipo de operacion
    List<Auditoria> findByUsuario(String usuario);
    List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion);

    List<Auditoria> findByEntidadAfectada(String entidadAfectada);
    List<Auditoria> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}