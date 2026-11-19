package CampusLands.logitrack.dto.response;

import java.util.List;

public record ReporteDtoResponse(

        List<StockBodegaDtoResponse> stockPorBodega,
        List<ProductoMovidoDtoResponse> productosMasMovidos

) {
}
