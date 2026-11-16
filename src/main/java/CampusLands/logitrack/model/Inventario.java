package CampusLands.logitrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bodega donde esta guardado el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "bodega_id")
    private Bodega bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "producto_id")
    private Producto producto;

    // Cuantas unidades de ese producto hay en esa bodega
    @Column(nullable = false)
    private Integer cantidad;
}