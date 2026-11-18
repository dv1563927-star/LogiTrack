package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.request.ProductoDtoRequest;
import CampusLands.logitrack.dto.response.ProductoDtoResponse;

import java.util.List;

public interface ProductoService {

    ProductoDtoResponse guardar(ProductoDtoRequest dto);
    ProductoDtoResponse actualizar(ProductoDtoRequest dto, Long id);
    void eliminar(Long id);
    List<ProductoDtoResponse> listar();
    ProductoDtoResponse buscarId(Long id);
    List<ProductoDtoResponse> buscarCategoria(String categoria);

    // Requisito 6: productos con stock bajo
    List<ProductoDtoResponse> listarStockBajo(Integer stock);
}
