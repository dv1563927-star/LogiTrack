package CampusLands.logitrack.service;

import CampusLands.logitrack.dto.response.ReporteDtoResponse;

public interface ReporteService {

    // Requisito 6: reporte de resumen general (stock por bodega y productos mas movidos)
    ReporteDtoResponse resumenGeneral();
}
