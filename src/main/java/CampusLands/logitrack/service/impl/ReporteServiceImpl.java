package CampusLands.logitrack.service.impl;

import CampusLands.logitrack.dto.response.ProductoMovidoDtoResponse;
import CampusLands.logitrack.dto.response.ReporteDtoResponse;
import CampusLands.logitrack.dto.response.StockBodegaDtoResponse;
import CampusLands.logitrack.model.DetalleMovimiento;
import CampusLands.logitrack.model.Inventario;
import CampusLands.logitrack.repository.BodegaRepository;
import CampusLands.logitrack.repository.DetalleMovimientoRepository;
import CampusLands.logitrack.repository.InventarioRepository;
import CampusLands.logitrack.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final BodegaRepository bodegaRepository;
    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    @Override
    public ReporteDtoResponse resumenGeneral() {

        // 1. Stock total (y valor) de cada bodega
        List<StockBodegaDtoResponse> stockPorBodega = bodegaRepository.findAll().stream().map(
                b -> {
                    List<Inventario> filas = inventarioRepository.findByBodegaId(b.getId());

                    int unidades = filas.stream()
                            .mapToInt(Inventario::getCantidad)
                            .sum();

                    BigDecimal valor = filas.stream()
                            .map(i -> BigDecimal.valueOf(i.getCantidad()).multiply(i.getProducto().getPrecio()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new StockBodegaDtoResponse(b.getNombre(), unidades, valor);
                }).toList();

        // 2. Los 5 productos con mas unidades movidas
        List<ProductoMovidoDtoResponse> productosMasMovidos = detalleMovimientoRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        d -> d.getProducto().getNombre(),
                        Collectors.summingInt(DetalleMovimiento::getCantidad)))
                .entrySet().stream()
                .map(e -> new ProductoMovidoDtoResponse(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.totalMovido() - a.totalMovido())
                .limit(5)
                .toList();

        return new ReporteDtoResponse(stockPorBodega, productosMasMovidos);
    }
}
