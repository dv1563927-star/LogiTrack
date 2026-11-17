package CampusLands.logitrack.dto.response;

public record InventarioDtoResponse(

        Long id, BodegaDtoResponse bodega, ProductoDtoResponseResumen producto, Integer cantidad

) {
}
