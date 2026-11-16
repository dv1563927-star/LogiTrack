package CampusLands.logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record InventarioDtoRequest(

        @Schema(description = "Codigo de la bodega", example = "1")
        @NotNull(message = "Error, el codigo de la bodega no puede ser nulo")
        @Positive(message = "Error, el codigo de la bodega debe ser positivo")
        Long bodegaId,

        @Schema(description = "Codigo del producto", example = "1")
        @NotNull(message = "Error, el codigo del producto no puede ser nulo")
        @Positive(message = "Error, el codigo del producto debe ser positivo")
        Long productoId,

        @Schema(description = "Unidades de ese producto en esa bodega", example = "15")
        @NotNull(message = "Error, la cantidad no puede ser nula")
        @Positive(message = "Error, la cantidad debe ser mayor a cero")
        Integer cantidad

) {
}