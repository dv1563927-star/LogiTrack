package CampusLands.logitrack.repository;

import CampusLands.logitrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    List<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);

}
