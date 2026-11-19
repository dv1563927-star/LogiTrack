package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.request.MovimientoDtoRequest;
import CampusLands.logitrack.dto.response.MovimientoDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.BodegaMapper;
import CampusLands.logitrack.mapper.MovimientoMapper;
import CampusLands.logitrack.mapper.UsuarioMapper;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.model.Movimiento;
import CampusLands.logitrack.model.TipoMovimiento;
import CampusLands.logitrack.model.Usuario;
import CampusLands.logitrack.repository.BodegaRepository;
import CampusLands.logitrack.repository.DetalleMovimientoRepository;
import CampusLands.logitrack.repository.MovimientoRepository;
import CampusLands.logitrack.repository.UsuarioRepository;
import CampusLands.logitrack.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoMapper movimientoMapper;
    private final UsuarioMapper usuarioMapper;
    private final BodegaMapper bodegaMapper;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    @Override
    public MovimientoDtoResponse guardar(MovimientoDtoRequest dto, String username) {

        List<Usuario> usuarios = usuarioRepository.findByUsername(username);
        if (usuarios.isEmpty()) {
            throw new BusinessRuleException("El usuario del token no existe");
        }
        Usuario usuario = usuarios.get(0);

        // Reglas segun el tipo de movimiento
        if (dto.tipo() == null) {
            throw new BusinessRuleException("El tipo de movimiento es obligatorio");
        }
        if (dto.tipo() == TipoMovimiento.ENTRADA) {
            if (dto.bodegaOrigenId() != null) {
                throw new BusinessRuleException("Una ENTRADA no puede tener bodega de origen");
            }
            if (dto.bodegaDestinoId() == null) {
                throw new BusinessRuleException("Una ENTRADA debe tener bodega de destino");
            }
        } else if (dto.tipo() == TipoMovimiento.SALIDA) {
            if (dto.bodegaOrigenId() == null) {
                throw new BusinessRuleException("Una SALIDA debe tener bodega de origen");
            }
            if (dto.bodegaDestinoId() != null) {
                throw new BusinessRuleException("Una SALIDA no puede tener bodega de destino");
            }
        } else {
            if (dto.bodegaOrigenId() == null || dto.bodegaDestinoId() == null) {
                throw new BusinessRuleException("Una TRANSFERENCIA debe tener bodega de origen y de destino");
            }
            if (dto.bodegaOrigenId().equals(dto.bodegaDestinoId())) {
                throw new BusinessRuleException("La bodega de origen y la de destino no pueden ser la misma");
            }
        }

        // Las bodegas pueden venir nulas segun el tipo
        Bodega origen = null;
        if (dto.bodegaOrigenId() != null) {
            origen = bodegaRepository.findById(dto.bodegaOrigenId())
                    .orElseThrow(() -> new BusinessRuleException("Codigo de bodega origen no existe"));
        }
        Bodega destino = null;
        if (dto.bodegaDestinoId() != null) {
            destino = bodegaRepository.findById(dto.bodegaDestinoId())
                    .orElseThrow(() -> new BusinessRuleException("Codigo de bodega destino no existe"));
        }

        Movimiento m = movimientoMapper.DtoToEntity(dto, usuario, origen, destino);
        Movimiento movimientoRespuesta = movimientoRepository.save(m);

        return movimientoMapper.entityToDto(movimientoRespuesta,
                usuarioMapper.entityToDto(movimientoRespuesta.getUsuario()),
                movimientoRespuesta.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(movimientoRespuesta.getBodegaOrigen()),
                movimientoRespuesta.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(movimientoRespuesta.getBodegaDestino()));
    }

    @Override
    public MovimientoDtoResponse actualizar(MovimientoDtoRequest dto, Long id, String username) {

        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, movimiento a actualizar no encontrado"));

        List<Usuario> usuarios = usuarioRepository.findByUsername(username);
        if (usuarios.isEmpty()) {
            throw new BusinessRuleException("El usuario del token no existe");
        }
        Usuario usuario = usuarios.get(0);

        if (dto.tipo() == null) {
            throw new BusinessRuleException("El tipo de movimiento es obligatorio");
        }
        if (dto.tipo() == TipoMovimiento.ENTRADA) {
            if (dto.bodegaOrigenId() != null) {
                throw new BusinessRuleException("Una ENTRADA no puede tener bodega de origen");
            }
            if (dto.bodegaDestinoId() == null) {
                throw new BusinessRuleException("Una ENTRADA debe tener bodega de destino");
            }
        } else if (dto.tipo() == TipoMovimiento.SALIDA) {
            if (dto.bodegaOrigenId() == null) {
                throw new BusinessRuleException("Una SALIDA debe tener bodega de origen");
            }
            if (dto.bodegaDestinoId() != null) {
                throw new BusinessRuleException("Una SALIDA no puede tener bodega de destino");
            }
        } else {
            if (dto.bodegaOrigenId() == null || dto.bodegaDestinoId() == null) {
                throw new BusinessRuleException("Una TRANSFERENCIA debe tener bodega de origen y de destino");
            }
            if (dto.bodegaOrigenId().equals(dto.bodegaDestinoId())) {
                throw new BusinessRuleException("La bodega de origen y la de destino no pueden ser la misma");
            }
        }

        Bodega origen = null;
        if (dto.bodegaOrigenId() != null) {
            origen = bodegaRepository.findById(dto.bodegaOrigenId())
                    .orElseThrow(() -> new BusinessRuleException("Codigo de bodega origen no existe"));
        }
        Bodega destino = null;
        if (dto.bodegaDestinoId() != null) {
            destino = bodegaRepository.findById(dto.bodegaDestinoId())
                    .orElseThrow(() -> new BusinessRuleException("Codigo de bodega destino no existe"));
        }

        movimientoMapper.updateMovimientoFromDto(m, dto, usuario, origen, destino);
        Movimiento movimientoActualizado = movimientoRepository.save(m);

        return movimientoMapper.entityToDto(movimientoActualizado,
                usuarioMapper.entityToDto(movimientoActualizado.getUsuario()),
                movimientoActualizado.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(movimientoActualizado.getBodegaOrigen()),
                movimientoActualizado.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(movimientoActualizado.getBodegaDestino()));
    }

    @Override
    public void eliminar(Long id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, movimiento a eliminar no existe"));

        // No se puede borrar un movimiento que ya tiene detalles
        if (detalleMovimientoRepository.existsByMovimientoId(m.getId())) {
            throw new BusinessRuleException("Este movimiento tiene detalles asociados, no se puede eliminar");
        }
        movimientoRepository.delete(m);
    }

    @Override
    public List<MovimientoDtoResponse> listar() {
        return movimientoRepository.findAll().stream().map(
                m -> movimientoMapper.entityToDto(m,
                        usuarioMapper.entityToDto(m.getUsuario()),
                        m.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(m.getBodegaOrigen()),
                        m.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(m.getBodegaDestino()))).toList();
    }

    @Override
    public MovimientoDtoResponse buscarId(Long id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe movimiento con ese id"));

        return movimientoMapper.entityToDto(m,
                usuarioMapper.entityToDto(m.getUsuario()),
                m.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(m.getBodegaOrigen()),
                m.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(m.getBodegaDestino()));
    }

    @Override
    public List<MovimientoDtoResponse> buscarTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipo(tipo).stream().map(
                m -> movimientoMapper.entityToDto(m,
                        usuarioMapper.entityToDto(m.getUsuario()),
                        m.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(m.getBodegaOrigen()),
                        m.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(m.getBodegaDestino()))).toList();
    }

    @Override
    public List<MovimientoDtoResponse> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin).stream().map(
                m -> movimientoMapper.entityToDto(m,
                        usuarioMapper.entityToDto(m.getUsuario()),
                        m.getBodegaOrigen() == null ? null : bodegaMapper.entityToDto(m.getBodegaOrigen()),
                        m.getBodegaDestino() == null ? null : bodegaMapper.entityToDto(m.getBodegaDestino()))).toList();
    }
}
