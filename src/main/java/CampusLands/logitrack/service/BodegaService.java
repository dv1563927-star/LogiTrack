package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.request.BodegaDtoRequest;
import CampusLands.logitrack.dto.response.BodegaDtoResponse;

import java.util.List;

public interface BodegaService {

    BodegaDtoResponse guardar(BodegaDtoRequest dto);
    BodegaDtoResponse actualizar(BodegaDtoRequest dto, Long id);
    void eliminar(Long id);
    List<BodegaDtoResponse> listar();
    BodegaDtoResponse buscarId(Long id);
    List<BodegaDtoResponse> buscarUbicacion(String ubicacion);
}
