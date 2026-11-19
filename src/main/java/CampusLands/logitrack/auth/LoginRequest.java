package CampusLands.logitrack.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Error, el username no puede ser nulo")
        String username,

        @NotBlank(message = "Error, la contrasena no puede ser nula")
        String password

) {
}
