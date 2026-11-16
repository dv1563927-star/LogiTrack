package CampusLands.logitrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record DetalleMovimientoDtoRequest(

        @Schema(description = "Codigo del movimiento", example = "1")
        @NotNull(message = "Error, el codigo del movimiento no puede ser nulo")
        @Positive(message = "Error, el codigo del movimiento debe ser positivo")
        Long movimientoId,

        @Schema(description = "Codigo del producto", example = "3")
        @NotNull(message = "Error, el codigo del producto no puede ser nulo")
        @Positive(message = "Error, el codigo del producto debe ser positivo")
        Long productoId,

        @Schema(description = "Cuantas unidades se mueven", example = "5")
        @NotNull(message = "Error, la cantidad no puede ser nula")
        @Positive(message = "Error, la cantidad debe ser mayor a cero")
        Integer cantidad

) {
}