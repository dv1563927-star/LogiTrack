package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.InventarioDtoResponse;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.service.InventarioService;
import CampusLands.logitrack.dto.request.InventarioDtoRequest;
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
@Tag(name = "EndPoints para inventario" , description = " Realiza un CRUD a la entidad inventario sin exponerla")
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioRepository inventarioRepository;


    // GUARDAR INVENTARIO
    @Operation(summary = "Permite agregar el inventario ", description = "Registra el stock de un producto dentro de una bodega")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego el inventario correctamente")
            }
    )
    @PostMapping
    public ResponseEntity<InventarioDtoResponse> guardar(@Valid @RequestBody InventarioDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.guardar(dto));
    }


    // LISTAR INVENTARIO
    @Operation(summary = "Permite listar y mostrar el inventario ", description = "Busca el inventario existente y lo muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego el inventario correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<InventarioDtoResponse>>listar()
        {   return ResponseEntity.ok(inventarioService.listar());
    }

    // BUSCAR INVENTARIO POR SU ID
    @Operation(summary = "Buscar inventario por su ID ", description = " Busca un inventario por su identificador unico" )
    @Parameter(description = "ID del inventario")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Inventario encontrado ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(inventarioService.buscarId(id));
    }

    // BUSCAR INVENTARIO POR BODEGA
    // http://localhost:8080/api/inventario/bodega/1

    @Operation(summary = "Buscar inventario por bodega ", description = " Muestra todos los productos que hay en una bodega" )
    @Parameter(description = "ID de la bodega")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Inventario encontrado ")
            }
    )
    @GetMapping("/bodega/{id}")
    public List<InventarioDtoResponse>buscarBodega(@Valid @PathVariable Long id){
        return ResponseEntity.ok(inventarioService.buscarBodegaId(id)).getBody();
    }

    // BUSCAR INVENTARIO POR PRODUCTO
    // http://localhost:8080/api/inventario/producto/1

    @Operation(summary = "Buscar inventario por producto ", description = " Muestra en que bodegas esta un producto" )
    @Parameter(description = "ID del producto")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Inventario encontrado ")
            }
    )
    @GetMapping("/producto/{id}")
    public List<InventarioDtoResponse>buscarProducto(@Valid @PathVariable Long id){
        return ResponseEntity.ok(inventarioService.buscarProductoId(id)).getBody();
    }

    // ELIMINAR INVENTARIO
    // http://localhost:8080/api/inventario/2

    @Operation(summary = "Eliminar inventario por ID ", description = " Permite eliminar inventario dependiendo su identificador unico" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Inventario encontrado ")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@Valid @PathVariable Long id ) {
        inventarioService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ACTUALIZAR INVENTARIO
    // http://localhost:8080/api/inventario/1

    @Operation(summary = "Actualizar inventario ", description = " Actualiza la cantidad de un producto en una bodega" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Inventario encontrado ")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<InventarioDtoResponse> actualizar (@Valid @RequestBody InventarioDtoRequest dto , @Parameter(description = "id a actualizar") @PathVariable Long id){
        return ResponseEntity.ok(inventarioService.actualizar(dto,id));
    }

}
