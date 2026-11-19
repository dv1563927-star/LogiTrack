package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.response.UsuarioDtoResponse;
import CampusLands.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDtoResponse entityToDto(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDtoResponse(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getEstado());
    }

}
