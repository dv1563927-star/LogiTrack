package CampusLands.logitrack.mapper;

import CampusLands.logitrack.dto.request.MovimientoDtoRequest;
import CampusLands.logitrack.dto.response.BodegaDtoResponse;
import CampusLands.logitrack.dto.response.MovimientoDtoResponse;
import CampusLands.logitrack.dto.response.UsuarioDtoResponse;
import CampusLands.logitrack.model.Bodega;
import CampusLands.logitrack.model.Movimiento;
import CampusLands.logitrack.model.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovimientoMapper {

    //la bodegaOrigen y bodegaDestino pueden ser null
    //(La entrada no tiene origen y la salida no tiene destino)
    //por eso no valido, solo el movimiento y el user
    public MovimientoDtoResponse entityToDto(Movimiento movimiento, UsuarioDtoResponse usuarioDto, BodegaDtoResponse bodegaOrigenDto, BodegaDtoResponse bodegaDestinoDto) {
        if(movimiento == null || usuarioDto == null) return null;
        return new MovimientoDtoResponse(movimiento.getId(), movimiento.getFecha(), movimiento.getTipo(), usuarioDto, bodegaOrigenDto, bodegaDestinoDto);
    }

    public Movimiento DtoToEntity(MovimientoDtoRequest dto, Usuario usuario, Bodega bodegaOrigen, Bodega bodegaDestino) {
        if(dto == null || usuario == null) return null;
        Movimiento m = new Movimiento();
        //la fecha la pone el servidor, nunca el cliente
        m.setFecha(LocalDateTime.now());
        m.setTipo(dto.tipo());
        m.setUsuario(usuario);
        m.setBodegaOrigen(bodegaOrigen);
        m.setBodegaDestino(bodegaDestino);
        return m;
    }

    public void updateMovimientoFromDto(Movimiento movimiento, MovimientoDtoRequest dto, Usuario usuario, Bodega bodegaOrigen, Bodega bodegaDestino) {
        if(movimiento == null || dto == null || usuario == null) return;
        //la fecha no se toca cuando se actualiza
        movimiento.setTipo(dto.tipo());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
    }

}
