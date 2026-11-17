package CampusLands.logitrack.dto.response;

import CampusLands.logitrack.model.Rol;

public record UsuarioDtoResponse(

        Long id, String username, Rol rol

) {
}
