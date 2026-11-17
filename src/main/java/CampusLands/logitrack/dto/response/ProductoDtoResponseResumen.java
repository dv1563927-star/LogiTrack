package CampusLands.logitrack.dto.response;


import java.math.BigDecimal;

//este dto es el equivalente a factura en el de clase
public record ProductoDtoResponseResumen(

        Long id, String nombre, BigDecimal precio

) {
}
