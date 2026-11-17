package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.request.BodegaDtoRequest;
import CampusLands.logitrack.dto.response.BodegaDtoResponse;
import CampusLands.logitrack.model.Bodega;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public BodegaDtoResponse entityToDto(Bodega bodega) {
        if (bodega == null) return null;
        return new BodegaDtoResponse(bodega.getId(), bodega.getNombre(), bodega.getUbicacion(), bodega.getCapacidad(), bodega.getEncargado());
    }

    public Bodega DtoToEntity(BodegaDtoRequest dto) {
        if (dto == null) return null;
        Bodega b = new Bodega();
        b.setNombre(dto.nombre());
        b.setUbicacion(dto.ubicacion());
        b.setCapacidad(dto.capacidad());
        b.setEncargado(dto.encargado());
        return b;
    }

    public void updateEntityTFromDto(Bodega bodega, BodegaDtoRequest dto) {
        if (dto == null || bodega == null) return;
        bodega.setNombre(dto.nombre());
        bodega.setUbicacion(dto.ubicacion());
        bodega.setCapacidad(dto.capacidad());
        bodega.setEncargado(dto.encargado());

    }

}
