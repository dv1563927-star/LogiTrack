package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.request.DetalleMovimientoDtoRequest;
import CampusLands.logitrack.dto.response.DetalleMovimientoDtoResponse;
import CampusLands.logitrack.dto.response.MovimientoDtoResponse;
import CampusLands.logitrack.dto.response.ProductoDtoResponseResumen;
import CampusLands.logitrack.model.DetalleMovimiento;
import CampusLands.logitrack.model.Movimiento;
import CampusLands.logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class DetalleMovimientoMapper {

    public DetalleMovimientoDtoResponse entityToDto(DetalleMovimiento detalle, MovimientoDtoResponse movimientoDto, ProductoDtoResponseResumen productoDto) {
        if (detalle == null || movimientoDto == null || productoDto == null) return null;
        return new DetalleMovimientoDtoResponse(detalle.getId(), movimientoDto, productoDto, detalle.getCantidad());
    }

    public DetalleMovimiento DtoToEntity(DetalleMovimientoDtoRequest dto, Movimiento movimiento, Producto producto) {
        if (dto == null || movimiento == null || producto == null) return null;
        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.cantidad());
        return detalle;
    }

    public void updateDetalleMovimientoFromDto(DetalleMovimiento detalle, DetalleMovimientoDtoRequest dto, Movimiento movimiento, Producto producto) {
        if (detalle == null || dto == null || movimiento == null || producto == null) return;
        detalle.setMovimiento(movimiento);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.cantidad());
    }
}