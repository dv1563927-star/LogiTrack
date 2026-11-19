package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.AuditoriaDtoResponse;
import CampusLands.logitrack.repository.AuditoriaRepository;
import CampusLands.logitrack.service.AuditoriaService;
import CampusLands.logitrack.model.TipoOperacion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Validated
@Tag(name = "EndPoints para auditorias" , description = " Registro de cambios hechos sobre las entidades")
@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;
    private final AuditoriaRepository auditoriaRepository;


    // LISTAR AUDITORIAS
    @Operation(summary = "Permite listar y mostrar las auditorias ", description = "Busca las auditorias existentes y las muestra ")

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = "Se agrego una auditoria correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<AuditoriaDtoResponse>>listar()
        {   return ResponseEntity.ok(auditoriaService.listar());
    }

    // BUSCAR AUDITORIAS POR SU ID
    @Operation(summary = "Buscar auditoria por su ID ", description = " Busca una auditoria por su identificador unico" )
    @Parameter(description = "ID de la auditoria")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Auditoria encontrada ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaDtoResponse>buscar(@Valid @PathVariable Long id){
        return ResponseEntity.ok(auditoriaService.buscarId(id));
    }

    // BUSCAR AUDITORIAS POR USUARIO
    // http://localhost:8080/api/auditoria/filtroUsuario?usuario=admin

    @Operation(summary = "Buscar auditoria por usuario ", description = " Busca las acciones hechas por un usuario" )
    @Parameter(description = "Nombre del usuario")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Auditoria encontrada ")
            }
    )
    @GetMapping("/filtroUsuario")
    public List<AuditoriaDtoResponse>buscarUsuario(@Valid @RequestParam String usuario){
        return ResponseEntity.ok(auditoriaService.buscarUsuario(usuario)).getBody();
    }

    // BUSCAR AUDITORIAS POR TIPO DE OPERACION
    // http://localhost:8080/api/auditoria/filtroTipo?tipo=INSERT

    @Operation(summary = "Buscar auditoria por tipo de operacion ", description = " Busca las acciones por INSERT, UPDATE o DELETE" )
    @Parameter(description = "Tipo de operacion")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Auditoria encontrada ")
            }
    )
    @GetMapping("/filtroTipo")
    public List<AuditoriaDtoResponse>buscarTipo(@Valid @RequestParam TipoOperacion tipo){
        return ResponseEntity.ok(auditoriaService.buscarTipoOperacion(tipo)).getBody();
    }

    // BUSCAR AUDITORIAS POR RANGO DE FECHAS
    // http://localhost:8080/api/auditoria/fechas?inicio=2026-11-01T00:00:00&fin=2026-11-30T23:59:59

    @Operation(summary = "Buscar auditorias por fechas ", description = " Busca auditorias entre dos fechas con BETWEEN" )
    @Parameter(description = "Fecha inicial en formato ISO")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Auditoria encontrada ")
            }
    )
    @GetMapping("/fechas")
    public List<AuditoriaDtoResponse>buscarPorFechas(@Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                     @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin){
        return ResponseEntity.ok(auditoriaService.buscarPorFechas(inicio, fin)).getBody();
    }

}
