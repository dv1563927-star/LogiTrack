package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.request.InventarioDtoRequest;
import CampusLands.logitrack.dto.response.InventarioDtoResponse;

import java.util.List;

public interface InventarioService {

    InventarioDtoResponse guardar(InventarioDtoRequest dto);
    InventarioDtoResponse actualizar(InventarioDtoRequest dto, Long id);
    void eliminar(Long id);
    List<InventarioDtoResponse> listar();
    InventarioDtoResponse buscarId(Long id);
    List<InventarioDtoResponse> buscarBodegaId(Long id);
    List<InventarioDtoResponse> buscarProductoId(Long id);
}
