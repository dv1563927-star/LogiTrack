package CampusLands.logitrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOperacion tipoOperacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Username de quien hizo la accion. No es llave foranea:
    // se guarda el texto para que la auditoria no se rompa si el usuario se borra.
    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "entidad_afectada", nullable = false, length = 50)
    private String entidadAfectada;

    // Se guardan como texto para no atarse a un formato fijo
    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos", columnDefinition = "TEXT")
    private String valoresNuevos;
}