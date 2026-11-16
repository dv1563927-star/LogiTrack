package CampusLands.logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductoDtoRequest(

        @Schema(description = "Nombre del producto", example = "Teclado Mecanico")
        @NotBlank(message = "Error, el nombre no puede ser nulo")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Categoria del producto", example = "Perifericos")
        @NotBlank(message = "Error, la categoria no puede ser nula")
        @Size(min = 2, max = 50, message = "La categoria debe tener entre 2 y 50 caracteres")
        String categoria,

        @Schema(description = "Cantidad total en todas las bodegas", example = "8")
        @NotNull(message = "Error, el stock no puede ser nulo")
        @Min(value = 0, message = "Error, el stock no puede ser negativo")
        Integer stock,

        @Schema(description = "Precio de venta del producto", example = "250000")
        @NotNull(message = "Error, el precio no puede ser nulo")
        @Positive(message = "Error, el precio debe ser mayor a cero")
        BigDecimal precio

) {
}
