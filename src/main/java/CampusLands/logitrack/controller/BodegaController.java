package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.BodegaDtoResponse;
import CampusLands.logitrack.repository.BodegaRepository;
import CampusLands.logitrack.service.BodegaService;
import CampusLands.logitrack.dto.request.BodegaDtoRequest;
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
@Tag(name = "EndPoints para bodegas" , description = " Realiza un CRUD a la entidad bodegas sin exponerla")
@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;
    private final BodegaRepository bodegaRepository;


    // GUARDAR BODEGA
    @Operation(summary = "Permite agregar las bodegas ", description = "Recibe los datos de la bodega para asi agregarla a la base de datos")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego una bodega correctamente")
            }
    )
    @PostMapping
    public ResponseEntity<BodegaDtoResponse> guardar(@Valid @RequestBody BodegaDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardar(dto));
    }


    // LISTAR BODEGAS
    @Operation(summary = "Permite listar y mostrar las bodegas ", description = "Busca las bodegas existentes y las muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego una bodega correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<BodegaDtoResponse>>listar()
        {   return ResponseEntity.ok(bodegaService.listar());
    }

    // BUSCAR BODEGAS POR SU ID
    @Operation(summary = "Buscar bodega por su ID ", description = " Busca una bodega por su identificador unico" )
    @Parameter(description = "ID de la bodega")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Bodega encontrada ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(bodegaService.buscarId(id));
    }

    // BUSCAR BODEGA POR SU UBICACION
    // http://localhost:8080/api/bodega/filtroUbicacion?ubicacion=Bogota

    @Operation(summary = "Buscar bodega por su ubicacion ", description = " Busca una bodega dependiendo de su ciudad" )
    @Parameter(description = "Ubicacion de la bodega")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Bodega encontrada ")
            }
    )
    @GetMapping("/filtroUbicacion")
    public List<BodegaDtoResponse>buscarUbicacion(@Valid @RequestParam String ubicacion){
        return ResponseEntity.ok(bodegaService.buscarUbicacion(ubicacion)).getBody();
    }

    // ELIMINAR BODEGAS
    // http://localhost:8080/api/bodega/2

    @Operation(summary = "Eliminar bodega por ID ", description = " Permite eliminar bodegas dependiendo su identificador unico" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Bodega encontrada ")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@Valid @PathVariable Long id ) {
        bodegaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ACTUALIZAR BODEGAS
    // http://localhost:8080/api/bodega/1

    @Operation(summary = "Actualizar bodega ", description = " Busca una bodega dependiendo de su ubicacion" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Bodega encontrada ")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse> actualizar (@Valid @RequestBody BodegaDtoRequest dto , @Parameter(description = "id a actualizar") @PathVariable Long id){
        return ResponseEntity.ok(bodegaService.actualizar(dto,id));
    }

}
