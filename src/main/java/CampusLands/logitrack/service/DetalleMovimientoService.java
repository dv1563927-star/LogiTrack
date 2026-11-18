package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.request.DetalleMovimientoDtoRequest;
import CampusLands.logitrack.dto.response.DetalleMovimientoDtoResponse;

import java.util.List;

public interface DetalleMovimientoService {

    DetalleMovimientoDtoResponse guardar(DetalleMovimientoDtoRequest dto);
    DetalleMovimientoDtoResponse actualizar(DetalleMovimientoDtoRequest dto, Long id);
    void eliminar(Long id);
    List<DetalleMovimientoDtoResponse> listar();
    DetalleMovimientoDtoResponse buscarId(Long id);
    List<DetalleMovimientoDtoResponse> buscarMovimientoId(Long id);
    List<DetalleMovimientoDtoResponse> buscarProductoId(Long id);
}
