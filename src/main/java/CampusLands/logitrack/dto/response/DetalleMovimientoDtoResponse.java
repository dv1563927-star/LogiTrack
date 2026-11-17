package CampusLands.logitrack.dto.response;

public record DetalleMovimientoDtoResponse(

        Long id, MovimientoDtoResponse movimiento, ProductoDtoResponseResumen producto, Integer cantidad

) {
}
