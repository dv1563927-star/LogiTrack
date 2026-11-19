package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.ProductoDtoResponse;
import CampusLands.logitrack.repository.ProductoRepository;
import CampusLands.logitrack.service.ProductoService;
import CampusLands.logitrack.dto.request.ProductoDtoRequest;
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
@Tag(name = "EndPoints para productos" , description = " Realiza un CRUD a la entidad productos sin exponerla")
@RestController
@RequestMapping("/api/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoRepository productoRepository;


    // GUARDAR PRODUCTO
    @Operation(summary = "Permite agregar los productos ", description = "Recibe los datos del producto para asi agregarlo a la base de datos")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un producto correctamente")
            }
    )
    @PostMapping
    public ResponseEntity<ProductoDtoResponse> guardar(@Valid @RequestBody ProductoDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(dto));
    }


    // LISTAR PRODUCTOS
    @Operation(summary = "Permite listar y mostrar los productos ", description = "Busca los productos existentes y los muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego un producto correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductoDtoResponse>>listar()
        {   return ResponseEntity.ok(productoService.listar());
    }

    // BUSCAR PRODUCTOS POR SU ID
    @Operation(summary = "Buscar producto por su ID ", description = " Busca un producto por su identificador unico" )
    @Parameter(description = "ID del producto")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Producto encontrado ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(productoService.buscarId(id));
    }

    // BUSCAR PRODUCTO POR SU CATEGORIA
    // http://localhost:8080/api/producto/filtroCategoria?categoria=Perifericos

    @Operation(summary = "Buscar producto por su categoria ", description = " Busca un producto dependiendo de su categoria" )
    @Parameter(description = "Categoria del producto")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Producto encontrado ")
            }
    )
    @GetMapping("/filtroCategoria")
    public List<ProductoDtoResponse>buscarCategoria(@Valid @RequestParam String categoria){
        return ResponseEntity.ok(productoService.buscarCategoria(categoria)).getBody();
    }

    // BUSCAR PRODUCTOS CON STOCK BAJO
    // http://localhost:8080/api/producto/stockBajo?stock=10

    @Operation(summary = "Buscar productos con stock bajo ", description = " Busca los productos con stock menor al indicado" )
    @Parameter(description = "Limite de stock")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Producto encontrado ")
            }
    )
    @GetMapping("/stockBajo")
    public List<ProductoDtoResponse>listarStockBajo(@Valid @RequestParam Integer stock){
        return ResponseEntity.ok(productoService.listarStockBajo(stock)).getBody();
    }

    // ELIMINAR PRODUCTOS
    // http://localhost:8080/api/producto/2

    @Operation(summary = "Eliminar producto por ID ", description = " Permite eliminar productos dependiendo su identificador unico" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Producto encontrado ")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar (@Valid @PathVariable Long id ) {
        productoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ACTUALIZAR PRODUCTOS
    // http://localhost:8080/api/producto/1

    @Operation(summary = "Actualizar producto ", description = " Busca un producto dependiendo de su categoria" )
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Producto encontrado ")
            }
    )

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> actualizar (@Valid @RequestBody ProductoDtoRequest dto , @Parameter(description = "id a actualizar") @PathVariable Long id){
        return ResponseEntity.ok(productoService.actualizar(dto,id));
    }

}
