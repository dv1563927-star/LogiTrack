package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.request.BodegaDtoRequest;
import CampusLands.logitrack.dto.response.BodegaDtoResponse;
import CampusLands.logitrack.exception.BusinessRuleException;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.BodegaMapper;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.repository.BodegaRepository;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaMapper bodegaMapper;
    private final BodegaRepository bodegaRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    public BodegaDtoResponse guardar(BodegaDtoRequest dto) {
        Bodega b = bodegaMapper.DtoToEntity(dto);
        Bodega bodegaRespuesta = bodegaRepository.save(b);
        return bodegaMapper.entityToDto(bodegaRespuesta);
    }

    @Override
    public BodegaDtoResponse actualizar(BodegaDtoRequest dto, Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, bodega a actualizar no encontrada"));
        bodegaMapper.updateEntityTFromDto(b, dto);
        return bodegaMapper.entityToDto(bodegaRepository.save(b));
    }

    @Override
    public void eliminar(Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Error, bodega a eliminar no existe"));

        // No se puede borrar una bodega que todavia tiene inventario
        if (inventarioRepository.existsByBodegaId(b.getId())) {
            throw new BusinessRuleException("Esta bodega tiene inventario asociado, no se puede eliminar");
        }
        bodegaRepository.delete(b);
    }

    @Override
    public List<BodegaDtoResponse> listar() {
        return bodegaRepository.findAll().stream().map(bodegaMapper::entityToDto).toList();
    }

    @Override
    public BodegaDtoResponse buscarId(Long id) {
        return bodegaMapper.entityToDto(bodegaRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe bodega con ese id")));
    }

    @Override
    public List<BodegaDtoResponse> buscarUbicacion(String ubicacion) {
        return bodegaRepository.findByUbicacion(ubicacion).stream().map(bodegaMapper::entityToDto).toList();
    }
}
