package CampusLands.logitrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bodega")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String ubicacion;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false, length = 100)
    private String encargado;

}
