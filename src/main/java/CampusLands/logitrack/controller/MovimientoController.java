package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.MovimientoDtoResponse;
import CampusLands.logitrack.repository.MovimientoRepository;
import CampusLands.logitrack.service.MovimientoService;
import CampusLands.logitrack.dto.request.MovimientoDtoRequest;
import CampusLands.logitrack.model.TipoMovimiento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@Validated
@Tag(name = "EndPoints para movimientos" , description = " Realiza un CRUD a la entidad movimientos sin exponerla")
@RestController
@RequestMapping("/api/movimiento")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final MovimientoRepository movimientoRepository;


    // GUARDAR MOVIMIENTO
    @Operation(summary = "Permite agregar los movimientos ", description = "El usuario responsable se toma del token JWT")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un movimiento correctamente")
            }
    )
    @PostMapping
    public ResponseEntity<MovimientoDtoResponse> guardar(@Valid @RequestBody MovimientoDtoRequest dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.guardar(dto, principal.getName()));
    }


    // LISTAR MOVIMIENTOS
    @Operation(summary = "Permite listar y mostrar los movimientos ", description = "Busca los movimientos existentes y los muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un movimiento correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<MovimientoDtoResponse>>listar()
        {   return ResponseEntity.ok(movimientoService.listar());
    }

    // BUSCAR MOVIMIENTOS POR SU ID
    @Operation(summary = "Buscar movimiento por su ID ", description = " Busca un movimiento por su identificador unico" )
    @Parameter(description = "ID del movimiento")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Movimiento encontrado ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(movimientoService.buscarId(id));
    }

    // BUSCAR MOVIMIENTOS POR TIPO
    // http://localhost:8080/api/movimiento/filtroTipo?tipo=ENTRADA

    @Operation(summary = "Buscar movimiento por su tipo ", description = " Busca movimientos dependiendo de su tipo" )
    @Parameter(description = "Tipo de movimiento")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Movimiento encontrado ")
            }
    )
    @GetMapping("/filtroTipo")
    public List<MovimientoDtoResponse>buscarTipo(@Valid @RequestParam TipoMovimiento tipo){
        return ResponseEntity.ok(movimientoService.buscarTipo(tipo)).getBody();
    }

    // BUSCAR MOVIMIENTOS POR RANGO DE FECHAS
    // http://localhost:8080/api/movimiento/fechas?inicio=2026-11-01T00:00:00&fin=2026-11-30T23:59:59

    @Operation(summary = "Buscar movimientos por fechas ", description = " Busca movimientos entre dos fechas con BETWEEN" )
    @Parameter(description = "Fecha inicial en formato ISO")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Movimiento encontrado ")
            }
    )
    @GetMapping("/fechas")
    public List<MovimientoDtoResponse>buscarPorFechas(@Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                      @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin){
        return ResponseEntity.ok(movimientoService.buscarPorFechas(inicio, fin)).getBody();
    }

    // ELIMINAR MOVIMIENTOS
    // http://localhost:8080/api/movimiento/2

    @Operation(summary = "Eliminar movimiento por ID ", description = " Permite eliminar movimientos dependiendo su identificador unico" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Movimiento encontrado ")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@Valid @PathVariable Long id ) {
        movimientoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ACTUALIZAR MOVIMIENTOS
    // http://localhost:8080/api/movimiento/1

    @Operation(summary = "Actualizar movimiento ", description = " Busca un movimiento dependiendo de su tipo" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Movimiento encontrado ")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDtoResponse> actualizar (@Valid @RequestBody MovimientoDtoRequest dto , @Parameter(description = "id a actualizar") @PathVariable Long id, Principal principal){
        return ResponseEntity.ok(movimientoService.actualizar(dto,id, principal.getName()));
    }

}
