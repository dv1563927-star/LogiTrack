package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.request.MovimientoDtoRequest;
import CampusLands.logitrack.dto.response.MovimientoDtoResponse;
import CampusLands.logitrack.model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {

    // El username lo manda el controller, sacado del token JWT
    MovimientoDtoResponse guardar(MovimientoDtoRequest dto, String username);
    MovimientoDtoResponse actualizar(MovimientoDtoRequest dto, Long id, String username);
    void eliminar(Long id);
    List<MovimientoDtoResponse> listar();
    MovimientoDtoResponse buscarId(Long id);
    List<MovimientoDtoResponse> buscarTipo(TipoMovimiento tipo);

    // Requisito 6: movimientos por rango de fechas
    List<MovimientoDtoResponse> buscarPorFechas(LocalDateTime inicio, LocalDateTime fin);
}
