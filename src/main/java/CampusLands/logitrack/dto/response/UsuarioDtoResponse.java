package CampusLands.logitrack.dto.response;

import CampusLands.logitrack.model.EstadoUsuario;
import CampusLands.logitrack.model.Rol;

public record UsuarioDtoResponse(

        Long id, String username, String nombre, String telefono, Rol rol, EstadoUsuario estado

) {
}
