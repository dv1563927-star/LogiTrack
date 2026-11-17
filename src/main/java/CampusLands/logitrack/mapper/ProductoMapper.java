package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.request.ProductoDtoRequest;
import CampusLands.logitrack.dto.response.ProductoDtoResponse;
import CampusLands.logitrack.dto.response.ProductoDtoResponseResumen;
import CampusLands.logitrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDtoResponse entityToDto(Producto producto) {
        if (producto == null) return null;
        return new ProductoDtoResponse(producto.getId(), producto.getNombre(), producto.getCategoria(), producto.getStock(), producto.getPrecio());
    }

    //este es el equivalente al entityToDtoFactura
    public ProductoDtoResponseResumen entityToDtoResumen(Producto producto) {
        if (producto == null) return null;
        return new ProductoDtoResponseResumen(producto.getId(), producto.getNombre(), producto.getPrecio());
    }

    public Producto DtoToEntity(ProductoDtoRequest dto) {
        if (dto == null) return null;
        Producto p = new Producto();
        p.setNombre(dto.nombre());
        p.setCategoria(dto.categoria());
        p.setStock(dto.stock());
        p.setPrecio(dto.precio());
        return p;
    }

    public void updateEntityTFromDto(Producto producto, ProductoDtoRequest dto) {
        if (dto == null || producto == null) return;
        producto.setNombre(dto.nombre());
        producto.setCategoria(dto.categoria());
        producto.setStock(dto.stock());
        producto.setPrecio(dto.precio());
    }

}
