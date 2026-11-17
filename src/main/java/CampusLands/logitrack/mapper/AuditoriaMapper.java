package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.response.AuditoriaDtoResponse;
import CampusLands.logitrack.model.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaDtoResponse entityToDto(Auditoria auditoria) {
        if (auditoria == null) return null;
        return new AuditoriaDtoResponse(auditoria.getId(), auditoria.getTipoOperacion(), auditoria.getFechaHora(),
                auditoria.getUsuario(), auditoria.getEntidadAfectada(),
                auditoria.getValoresAnteriores(), auditoria.getValoresNuevos());
    }
}