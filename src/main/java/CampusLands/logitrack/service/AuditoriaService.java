package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.response.AuditoriaDtoResponse;
import CampusLands.logitrack.model.TipoOperacion;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaService {

    List<AuditoriaDtoResponse> listar();
    AuditoriaDtoResponse buscarId(Long id);

    // Requisito 6: auditorias por usuario o por tipo de operacion
    List<AuditoriaDtoResponse> buscarUsuario(String usuario);
    List<AuditoriaDtoResponse> buscarTipoOperacion(TipoOperacion tipoOperacion);
    List<AuditoriaDtoResponse> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin);
}
