package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.request.InventarioDtoRequest;
import CampusLands.logitrack.dto.response.InventarioDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.BodegaMapper;
import CampusLands.logitrack.mapper.InventarioMapper;
import CampusLands.logitrack.mapper.ProductoMapper;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.model.Inventario;
import CampusLands.logitrack.model.Producto;
import CampusLands.logitrack.repository.BodegaRepository;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.repository.ProductoRepository;
import CampusLands.logitrack.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioMapper inventarioMapper;
    private final BodegaMapper bodegaMapper;
    private final ProductoMapper productoMapper;
    private final InventarioRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public InventarioDtoResponse guardar(InventarioDtoRequest dto) {

        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de bodega no existe"));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de producto no existe"));

        // Un producto solo puede tener una fila por bodega (uq_inventario)
        List<Inventario> existente = inventarioRepository.findByBodegaIdAndProductoId(bodega.getId(), producto.getId());
        if (!existente.isEmpty()) {
            throw new BusinessRuleException("Ya existe inventario de ese producto en esa bodega. Use actualizar.");
        }

        Inventario inventario = inventarioMapper.DtoToEntity(dto, bodega, producto);
        Inventario inventarioRespuesta = inventarioRepository.save(inventario);

        return inventarioMapper.entityToDto(inventarioRespuesta,
                bodegaMapper.entityToDto(inventarioRespuesta.getBodega()),
                productoMapper.entityToDtoResumen(inventarioRespuesta.getProducto()));
    }

    @Override
    public InventarioDtoResponse actualizar(InventarioDtoRequest dto, Long id) {

        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, inventario a actualizar no encontrado"));

        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de bodega no existe"));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de producto no existe"));

        inventarioMapper.updateInventarioFromDto(inventario, dto, bodega, producto);
        Inventario inventarioActualizado = inventarioRepository.save(inventario);

        return inventarioMapper.entityToDto(inventarioActualizado,
                bodegaMapper.entityToDto(inventarioActualizado.getBodega()),
                productoMapper.entityToDtoResumen(inventarioActualizado.getProducto()));
    }

    @Override
    public void eliminar(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, inventario a eliminar no existe"));
        inventarioRepository.delete(inventario);
    }

    @Override
    public List<InventarioDtoResponse> listar() {
        return inventarioRepository.findAll().stream().map(
                i -> inventarioMapper.entityToDto(i,
                        bodegaMapper.entityToDto(i.getBodega()),
                        productoMapper.entityToDtoResumen(i.getProducto()))).toList();
    }

    @Override
    public InventarioDtoResponse buscarId(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe inventario con ese id"));

        return inventarioMapper.entityToDto(inventario,
                bodegaMapper.entityToDto(inventario.getBodega()),
                productoMapper.entityToDtoResumen(inventario.getProducto()));
    }

    @Override
    public List<InventarioDtoResponse> buscarBodegaId(Long id) {
        return inventarioRepository.findByBodegaId(id).stream().map(
                i -> inventarioMapper.entityToDto(i,
                        bodegaMapper.entityToDto(i.getBodega()),
                        productoMapper.entityToDtoResumen(i.getProducto()))).toList();
    }

    @Override
    public List<InventarioDtoResponse> buscarProductoId(Long id) {
        return inventarioRepository.findByProductoId(id).stream().map(
                i -> inventarioMapper.entityToDto(i,
                        bodegaMapper.entityToDto(i.getBodega()),
                        productoMapper.entityToDtoResumen(i.getProducto()))).toList();
    }
}
