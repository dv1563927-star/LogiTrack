package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.DetalleMovimientoDtoResponse;
import CampusLands.logitrack.repository.DetalleMovimientoRepository;
import CampusLands.logitrack.service.DetalleMovimientoService;
import CampusLands.logitrack.dto.request.DetalleMovimientoDtoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@Tag(name = "EndPoints para detalle de movimiento" , description = " Realiza un CRUD a la entidad detalle movimiento sin exponerla")
@RestController
@RequestMapping("/api/detalle")
@RequiredArgsConstructor
public class DetalleMovimientoController {

    private final DetalleMovimientoService detalleMovimientoService;
    private final DetalleMovimientoRepository detalleMovimientoRepository;


    // GUARDAR DETALLE
    @Operation(summary = "Permite agregar los detalles ", description = "Al guardar el detalle se actualiza el stock de las bodegas")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un detalle correctamente")
            }
    )
    @PostMapping
    public ResponseEntity<DetalleMovimientoDtoResponse> guardar(@Valid @RequestBody DetalleMovimientoDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleMovimientoService.guardar(dto));
    }


    // LISTAR DETALLES
    @Operation(summary = "Permite listar y mostrar los detalles ", description = "Busca los detalles existentes y los muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un detalle correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<DetalleMovimientoDtoResponse>>listar()
        {   return ResponseEntity.ok(detalleMovimientoService.listar());
    }

    // BUSCAR DETALLES POR SU ID
    @Operation(summary = "Buscar detalle por su ID ", description = " Busca un detalle por su identificador unico" )
    @Parameter(description = "ID del detalle")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Detalle encontrado ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DetalleMovimientoDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(detalleMovimientoService.buscarId(id));
    }

    // BUSCAR DETALLES POR MOVIMIENTO
    // http://localhost:8080/api/detalle/movimiento/1

    @Operation(summary = "Buscar detalle por movimiento ", description = " Muestra los productos de un movimiento" )
    @Parameter(description = "ID del movimiento")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Detalle encontrado ")
            }
    )
    @GetMapping("/movimiento/{id}")
    public List<DetalleMovimientoDtoResponse>buscarMovimiento(@Valid @PathVariable Long id){
        return ResponseEntity.ok(detalleMovimientoService.buscarMovimientoId(id)).getBody();
    }

    // BUSCAR DETALLES POR PRODUCTO
    // http://localhost:8080/api/detalle/producto/1

    @Operation(summary = "Buscar detalle por producto ", description = " Muestra los movimientos donde aparece un producto" )
    @Parameter(description = "ID del producto")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Detalle encontrado ")
            }
    )
    @GetMapping("/producto/{id}")
    public List<DetalleMovimientoDtoResponse>buscarProducto(@Valid @PathVariable Long id){
        return ResponseEntity.ok(detalleMovimientoService.buscarProductoId(id)).getBody();
    }

    // ELIMINAR DETALLES
    // http://localhost:8080/api/detalle/2

    @Operation(summary = "Eliminar detalle por ID ", description = " Permite eliminar detalles dependiendo su identificador unico" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Detalle encontrado ")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@Valid @PathVariable Long id ) {
        detalleMovimientoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ACTUALIZAR DETALLES
    // http://localhost:8080/api/detalle/1

    @Operation(summary = "Actualizar detalle ", description = " Busca un detalle dependiendo de su movimiento" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Detalle encontrado ")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<DetalleMovimientoDtoResponse> actualizar (@Valid @RequestBody DetalleMovimientoDtoRequest dto , @Parameter(description = "id a actualizar") @PathVariable Long id){
        return ResponseEntity.ok(detalleMovimientoService.actualizar(dto,id));
    }

}
