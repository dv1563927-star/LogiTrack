package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    List<DetalleMovimiento> findByMovimientoId(Long movimientoId);
    List<DetalleMovimiento> findByProductoId(Long productoId);

    // Para validar antes de borrar
    boolean existsByMovimientoId(Long movimientoId);
    boolean existsByProductoId(Long productoId);
}