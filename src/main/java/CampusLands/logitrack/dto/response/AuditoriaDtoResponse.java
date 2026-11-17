package CampusLands.logitrack.dto.response;

import CampusLands.logitrack.model.TipoOperacion;

import java.time.LocalDateTime;

public record AuditoriaDtoResponse(

        Long id, TipoOperacion tipoOperacion, LocalDateTime fechaHora, String usuario, String entidadAfectada, String valoresAnteriores, String valoresNuevos

) {
}
