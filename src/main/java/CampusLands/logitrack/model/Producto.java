package CampusLands.logitrack.model;

import CampusLands.logitrack.config.AuditoriaListener;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "producto")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String categoria;

    // Stock TOTAL: la suma de lo que hay en todas las bodegas.
    // El detalle por bodega vive en la tabla inventario.
    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private BigDecimal precio;
}
