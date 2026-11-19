package CampusLands.logitrack.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Error, el username no puede ser nulo")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "Error, la contrasena no puede ser nula")
        @Size(min = 6, max = 100, message = "La contrasena debe tener entre 6 y 100 caracteres")
        String password,

        @NotBlank(message = "Error, el nombre no puede ser nulo")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "Error, el telefono no puede ser nulo")
        @Pattern(regexp = "[0-9 +()\\-]{7,20}",
                 message = "El telefono debe tener entre 7 y 20 caracteres (numeros, espacios o guiones)")
        String telefono

) {
}
