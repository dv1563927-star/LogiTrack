package CampusLands.logitrack.model;

import CampusLands.logitrack.config.AuditoriaListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "movimiento")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    // Empleado logueado que registro el movimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    // en una entrada no hay origen y en una salida no hay destino;
    // en la transferencia se usan ambas.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;
}
