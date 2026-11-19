package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.request.DetalleMovimientoDtoRequest;
import CampusLands.logitrack.dto.response.DetalleMovimientoDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.BodegaMapper;
import CampusLands.logitrack.mapper.DetalleMovimientoMapper;
import CampusLands.logitrack.mapper.MovimientoMapper;
import CampusLands.logitrack.mapper.ProductoMapper;
import CampusLands.logitrack.mapper.UsuarioMapper;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.model.DetalleMovimiento;
import CampusLands.logitrack.model.Inventario;
import CampusLands.logitrack.model.Movimiento;
import CampusLands.logitrack.model.Producto;
import CampusLands.logitrack.model.TipoMovimiento;
import CampusLands.logitrack.repository.DetalleMovimientoRepository;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.repository.MovimientoRepository;
import CampusLands.logitrack.repository.ProductoRepository;
import CampusLands.logitrack.service.DetalleMovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleMovimientoServiceImpl implements DetalleMovimientoService {

    private final DetalleMovimientoMapper detalleMovimientoMapper;
    private final MovimientoMapper movimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final BodegaMapper bodegaMapper;
    private final ProductoMapper productoMapper;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    public DetalleMovimientoDtoResponse guardar(DetalleMovimientoDtoRequest dto) {

        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de movimiento no existe"));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de producto no existe"));

        // Aqui es donde de verdad se mueve el inventario
        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {

            Bodega bodega = movimiento.getBodegaDestino();
            if (bodega == null) {
                throw new BusinessRuleException("La entrada no tiene bodega de destino");
            }

            List<Inventario> filas = inventarioRepository.findByBodegaIdAndProductoId(bodega.getId(), producto.getId());
            if (filas.isEmpty()) {
                Inventario nuevo = new Inventario();
                nuevo.setBodega(bodega);
                nuevo.setProducto(producto);
                nuevo.setCantidad(dto.cantidad());
                inventarioRepository.save(nuevo);
            } else {
                Inventario inventario = filas.get(0);
                inventario.setCantidad(inventario.getCantidad() + dto.cantidad());
                inventarioRepository.save(inventario);
            }

        } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {

            Bodega bodega = movimiento.getBodegaOrigen();
            if (bodega == null) {
                throw new BusinessRuleException("La salida no tiene bodega de origen");
            }

            List<Inventario> filas = inventarioRepository.findByBodegaIdAndProductoId(bodega.getId(), producto.getId());
            if (filas.isEmpty()) {
                throw new BusinessRuleException("No hay inventario de ese producto en la bodega indicada");
            }
            Inventario inventario = filas.get(0);
            if (inventario.getCantidad() < dto.cantidad()) {
                throw new BusinessRuleException("Stock insuficiente. Disponible en la bodega: " + inventario.getCantidad());
            }
            inventario.setCantidad(inventario.getCantidad() - dto.cantidad());
            inventarioRepository.save(inventario);

        } else {

            // TRANSFERENCIA: se resta del origen y se suma al destino
            Bodega origen = movimiento.getBodegaOrigen();
            Bodega destino = movimiento.getBodegaDestino();
            if (origen == null || destino == null) {
                throw new BusinessRuleException("La transferencia necesita bodega de origen y de destino");
            }

            List<Inventario> filasOrigen = inventarioRepository.findByBodegaIdAndProductoId(origen.getId(), producto.getId());
            if (filasOrigen.isEmpty()) {
                throw new BusinessRuleException("No hay inventario de ese producto en la bodega de origen");
            }
            Inventario inventarioOrigen = filasOrigen.get(0);
            if (inventarioOrigen.getCantidad() < dto.cantidad()) {
                throw new BusinessRuleException("Stock insuficiente en el origen. Disponible: " + inventarioOrigen.getCantidad());
            }
            inventarioOrigen.setCantidad(inventarioOrigen.getCantidad() - dto.cantidad());
            inventarioRepository.save(inventarioOrigen);

            List<Inventario> filasDestino = inventarioRepository.findByBodegaIdAndProductoId(destino.getId(), producto.getId());
            if (filasDestino.isEmpty()) {
                Inventario nuevo = new Inventario();
                nuevo.setBodega(destino);
                nuevo.setProducto(producto);
                nuevo.setCantidad(dto.cantidad());
                inventarioRepository.save(nuevo);
            } else {
                Inventario inventarioDestino = filasDestino.get(0);
                inventarioDestino.setCantidad(inventarioDestino.getCantidad() + dto.cantidad());
                inventarioRepository.save(inventarioDestino);
            }
        }

        // El stock total del producto es la suma de lo que hay en todas las bodegas
        int total = inventarioRepository.findByProductoId(producto.getId()).stream()
                .mapToInt(Inventario::getCantidad)
                .sum();
        producto.setStock(total);
        productoRepository.save(producto);

        DetalleMovimiento detalle = detalleMovimientoMapper.DtoToEntity(dto, movimiento, producto);
        DetalleMovimiento detalleRespuesta = detalleMovimientoRepository.save(detalle);

        return detalleMovimientoMapper.entityToDto(detalleRespuesta,
                movimientoMapper.entityToDto(detalleRespuesta.getMovimiento(),
                        usuarioMapper.entityToDto(detalleRespuesta.getMovimiento().getUsuario()),
                        detalleRespuesta.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(detalleRespuesta.getMovimiento().getBodegaOrigen()),
                        detalleRespuesta.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(detalleRespuesta.getMovimiento().getBodegaDestino())),
                productoMapper.entityToDtoResumen(detalleRespuesta.getProducto()));
    }

    @Override
    public DetalleMovimientoDtoResponse actualizar(DetalleMovimientoDtoRequest dto, Long id) {

        DetalleMovimiento detalle = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe el codigo del detalle de movimiento"));
        Movimiento movimiento = movimientoRepository.findById(dto.movimientoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de movimiento no existe"));
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new BusinessRuleException("Codigo de producto no existe"));

        detalleMovimientoMapper.updateDetalleMovimientoFromDto(detalle, dto, movimiento, producto);
        DetalleMovimiento detalleActualizado = detalleMovimientoRepository.save(detalle);

        return detalleMovimientoMapper.entityToDto(detalleActualizado,
                movimientoMapper.entityToDto(detalleActualizado.getMovimiento(),
                        usuarioMapper.entityToDto(detalleActualizado.getMovimiento().getUsuario()),
                        detalleActualizado.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(detalleActualizado.getMovimiento().getBodegaOrigen()),
                        detalleActualizado.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(detalleActualizado.getMovimiento().getBodegaDestino())),
                productoMapper.entityToDtoResumen(detalleActualizado.getProducto()));
    }

    @Override
    public void eliminar(Long id) {
        DetalleMovimiento detalle = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("No existe dicho detalle de movimiento"));
        detalleMovimientoRepository.delete(detalle);
    }

    @Override
    public List<DetalleMovimientoDtoResponse> listar() {
        return detalleMovimientoRepository.findAll().stream().map(
                d -> detalleMovimientoMapper.entityToDto(d,
                        movimientoMapper.entityToDto(d.getMovimiento(),
                                usuarioMapper.entityToDto(d.getMovimiento().getUsuario()),
                                d.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaOrigen()),
                                d.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaDestino())),
                        productoMapper.entityToDtoResumen(d.getProducto()))).toList();
    }

    @Override
    public DetalleMovimientoDtoResponse buscarId(Long id) {
        DetalleMovimiento detalle = detalleMovimientoRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe detalle con ese id"));

        return detalleMovimientoMapper.entityToDto(detalle,
                movimientoMapper.entityToDto(detalle.getMovimiento(),
                        usuarioMapper.entityToDto(detalle.getMovimiento().getUsuario()),
                        detalle.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(detalle.getMovimiento().getBodegaOrigen()),
                        detalle.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(detalle.getMovimiento().getBodegaDestino())),
                productoMapper.entityToDtoResumen(detalle.getProducto()));
    }

    @Override
    public List<DetalleMovimientoDtoResponse> buscarMovimientoId(Long id) {
        return detalleMovimientoRepository.findByMovimientoId(id).stream().map(
                d -> detalleMovimientoMapper.entityToDto(d,
                        movimientoMapper.entityToDto(d.getMovimiento(),
                                usuarioMapper.entityToDto(d.getMovimiento().getUsuario()),
                                d.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaOrigen()),
                                d.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaDestino())),
                        productoMapper.entityToDtoResumen(d.getProducto()))).toList();
    }

    @Override
    public List<DetalleMovimientoDtoResponse> buscarProductoId(Long id) {
        return detalleMovimientoRepository.findByProductoId(id).stream().map(
                d -> detalleMovimientoMapper.entityToDto(d,
                        movimientoMapper.entityToDto(d.getMovimiento(),
                                usuarioMapper.entityToDto(d.getMovimiento().getUsuario()),
                                d.getMovimiento().getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaOrigen()),
                                d.getMovimiento().getBodegaDestino() == null ? null : bodegaMapper.entityToDto(d.getMovimiento().getBodegaDestino())),
                        productoMapper.entityToDtoResumen(d.getProducto()))).toList();
    }
}
