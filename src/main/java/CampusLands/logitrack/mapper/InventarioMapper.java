package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.request.InventarioDtoRequest;
import CampusLands.logitrack.dto.response.BodegaDtoResponse;
import CampusLands.logitrack.dto.response.InventarioDtoResponse;
import CampusLands.logitrack.dto.response.ProductoDtoResponseResumen;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.model.Inventario;
import CampusLands.logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public InventarioDtoResponse entityToDto(Inventario inventario,
                                             BodegaDtoResponse bodegaDto,
                                             ProductoDtoResponseResumen productoDto) {
        if (inventario == null || bodegaDto == null || productoDto == null) return null;
        return new InventarioDtoResponse(inventario.getId(), bodegaDto, productoDto, inventario.getCantidad());
    }

    public Inventario DtoToEntity(InventarioDtoRequest dto, Bodega bodega, Producto producto) {
        if (dto == null || bodega == null || producto == null) return null;
        Inventario inventario = new Inventario();
        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setCantidad(dto.cantidad());
        return inventario;
    }

    public void updateInventarioFromDto(Inventario inventario, InventarioDtoRequest dto, Bodega bodega, Producto producto) {
        if (inventario == null || dto == null || bodega == null || producto == null) return;
        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setCantidad(dto.cantidad());
    }
}