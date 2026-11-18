package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.Movimiento;
import CampusLands.logitrack.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByTipo(TipoMovimiento tipo);
    List<Movimiento> findByUsuarioId(Long usuarioId);

    // Requisito 6: movimientos por rango de fechas (BETWEEN)
    List<Movimiento> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}