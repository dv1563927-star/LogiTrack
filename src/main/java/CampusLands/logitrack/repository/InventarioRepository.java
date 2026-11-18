package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByBodegaId(Long bodegaId);
    List<Inventario> findByProductoId(Long productoId);

    // La fila de "este producto en esta bodega"
    List<Inventario> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);

    // Para validar antes de borrar
    boolean existsByBodegaId(Long bodegaId);
    boolean existsByProductoId(Long productoId);
}