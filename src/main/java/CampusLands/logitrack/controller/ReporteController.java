package CampusLands.logitrack.controller;

import CampusLands.logitrack.dto.response.ReporteDtoResponse;
import CampusLands.logitrack.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@Tag(name = "EndPoints para reportes" , description = " Resumen general del sistema")
@RestController
@RequestMapping("/api/reporte")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;


    // REPORTE DE RESUMEN GENERAL
    // http://localhost:8080/api/reporte

    @Operation(summary = "Resumen general del sistema ", description = " Stock total por bodega y los productos mas movidos" )

    @ApiResponses(
            value = {@ApiResponse(responseCode = "201", description = " Reporte generado correctamente ")
            }
    )
    @GetMapping
    public ResponseEntity<ReporteDtoResponse> resumenGeneral() {
        return ResponseEntity.ok(reporteService.resumenGeneral());
    }

}
