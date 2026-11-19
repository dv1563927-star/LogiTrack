package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.request.ProductoDtoRequest;
import CampusLands.logitrack.dto.response.ProductoDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.ProductoMapper;
import CampusLands.logitrack.model.Producto;
import CampusLands.logitrack.repository.DetalleMovimientoRepository;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.repository.ProductoRepository;
import CampusLands.logitrack.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    @Override
    public ProductoDtoResponse guardar(ProductoDtoRequest dto) {
        Producto p = productoMapper.DtoToEntity(dto);
        Producto productoRespuesta = productoRepository.save(p);
        return productoMapper.entityToDto(productoRespuesta);
    }

    @Override
    public ProductoDtoResponse actualizar(ProductoDtoRequest dto, Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, producto a actualizar no encontrado"));
        productoMapper.updateEntityTFromDto(p, dto);
        return productoMapper.entityToDto(productoRepository.save(p));
    }

    @Override
    public void eliminar(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, producto a eliminar no existe"));

        // No se puede borrar un producto que ya esta en inventario o en un movimiento
        if (inventarioRepository.existsByProductoId(p.getId())) {
            throw new BusinessRuleException("Este producto tiene inventario asociado, no se puede eliminar");
        }
        if (detalleMovimientoRepository.existsByProductoId(p.getId())) {
            throw new BusinessRuleException("Este producto pertenece a un detalle de movimiento, no se puede eliminar");
        }
        productoRepository.delete(p);
    }

    @Override
    public List<ProductoDtoResponse> listar() {
        return productoRepository.findAll().stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public ProductoDtoResponse buscarId(Long id) {
        return productoMapper.entityToDto(productoRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe producto con ese id")));
    }

    @Override
    public List<ProductoDtoResponse> buscarCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoDtoResponse> listarStockBajo(Integer stock) {
        return productoRepository.findByStockLessThan(stock).stream().map(productoMapper::entityToDto).toList();
    }
}
