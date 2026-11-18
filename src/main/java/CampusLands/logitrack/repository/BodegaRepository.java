package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Long>{

    List<Bodega> findByUbicacion(String ubicacion);
    List<Bodega> findByNombreContainingIgnoreCase(String nombre);

}
