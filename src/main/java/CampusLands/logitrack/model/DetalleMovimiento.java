package CampusLands.logitrack.model;

import CampusLands.logitrack.config.AuditoriaListener;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "detalle_movimiento")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class DetalleMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "movimiento_id")
    private Movimiento movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "producto_id")
    private Producto producto;

    // Cuantas unidades de ese producto se mueven
    @Column(nullable = false)
    private Integer cantidad;
}