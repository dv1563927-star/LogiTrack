package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.response.AuditoriaDtoResponse;
import CampusLands.logitrack.exception.DatabaseRuleException;
import CampusLands.logitrack.mapper.AuditoriaMapper;
import CampusLands.logitrack.model.Auditoria;
import CampusLands.logitrack.model.TipoOperacion;
import CampusLands.logitrack.repository.AuditoriaRepository;
import CampusLands.logitrack.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaMapper auditoriaMapper;
    private final AuditoriaRepository auditoriaRepository;

    @Override
    public List<AuditoriaDtoResponse> listar() {
        return auditoriaRepository.findAll().stream().map(auditoriaMapper::entityToDto).toList();
    }

    @Override
    public AuditoriaDtoResponse buscarId(Long id) {
        return auditoriaMapper.entityToDto(auditoriaRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("Error, no existe auditoria con ese id")));
    }

    @Override
    public List<AuditoriaDtoResponse> buscarUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario).stream().map(auditoriaMapper::entityToDto).toList();
    }

    @Override
    public List<AuditoriaDtoResponse> buscarTipoOperacion(TipoOperacion tipoOperacion) {
        return auditoriaRepository.findByTipoOperacion(tipoOperacion).stream().map(auditoriaMapper::entityToDto).toList();
    }

    @Override
    public List<AuditoriaDtoResponse> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaHoraBetween(inicio, fin).stream().map(auditoriaMapper::entityToDto).toList();
    }
}
