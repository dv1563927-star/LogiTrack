package CampusLands.logitrack.dto.request;

import CampusLands.logitrack.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record MovimientoDtoRequest(

        @Schema(description = "Tipo de movimiento", example = "ENTRADA")
        @NotNull(message = "Error, el tipo de movimiento no puede ser nulo")
        TipoMovimiento tipo,

        // En una ENTRADA no hay origen: por eso NO lleva @NotNull
        @Schema(description = "Codigo de la bodega de origen (solo SALIDA y TRANSFERENCIA)", example = "1")
        @Positive(message = "Error, el codigo de la bodega origen debe ser positivo")
        Long bodegaOrigenId,

        // En una SALIDA no hay destino: por eso NO lleva @NotNull
        @Schema(description = "Codigo de la bodega de destino (solo ENTRADA y TRANSFERENCIA)", example = "2")
        @Positive(message = "Error, el codigo de la bodega destino debe ser positivo")
        Long bodegaDestinoId

) {
}