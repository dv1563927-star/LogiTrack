package CampusLands.logitrack.dto.response;

import java.math.BigDecimal;

public record StockBodegaDtoResponse(

        String bodega, Integer totalUnidades, BigDecimal valorTotal

) {
}
