package CampusLands.logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record BodegaDtoRequest(

        @Schema(description = "Nombre de la bodega", example = "Bodega Norte")
        @NotBlank(message = "Error, el nombre no puede ser nulo")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Ciudad donde esta la bodega", example = "Bogota")
        @NotBlank(message = "Error, la ubicacion no puede ser nula")
        @Size(min = 2, max = 100, message = "La ubicacion debe tener entre 2 y 100 caracteres")
        String ubicacion,

        @Schema(description = "Cuantas unidades caben en la bodega", example = "5000")
        @NotNull(message = "Error, la capacidad no puede ser nula")
        @Min(value = 1, message = "La capacidad debe ser mayor a cero")
        Integer capacidad,

        @Schema(description = "Persona encargada de la bodega", example = "Carlos Perez")
        @NotBlank(message = "Error, el encargado no puede ser nulo")
        @Size(min = 2, max = 100, message = "El encargado debe tener entre 2 y 100 caracteres")
        String encargado

) {
}
