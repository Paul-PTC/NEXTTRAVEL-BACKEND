package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ESTADO") // Oracle crea mayúsculas; columnas: IDESTADO, NOMBREESTADO
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDESTADO")
    private Long idEstado;

    @Column(name = "NOMBREESTADO", nullable = false, length = 100, unique = true)
    private String nombreEstado;
}
