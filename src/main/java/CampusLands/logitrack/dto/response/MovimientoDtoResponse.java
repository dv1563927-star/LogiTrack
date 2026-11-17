package CampusLands.logitrack.dto.response;

import CampusLands.logitrack.model.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoDtoResponse(

        Long id, LocalDateTime fecha, TipoMovimiento tipo, UsuarioDtoResponse usuario, BodegaDtoResponse bodegaOrigen, BodegaDtoResponse bodegaDestino

) {
}
