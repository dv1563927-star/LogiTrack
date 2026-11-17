package CampusLands.logitrack.dto.response;

import java.math.BigDecimal;

public record ProductoDtoResponse(

        Long id, String nombre, String categoria, int stock, BigDecimal precio

) {
}
