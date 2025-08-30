package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "RANGOEMPLEADO") // Oracle crea el nombre en mayúsculas y sin guiones bajos
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RangoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDRANGO")
    private Long idRango;

    @Column(name = "NOMBRERANGO", nullable = false, length = 100, unique = true)
    private String nombreRango;

    @Column(name = "SALARIOBASE", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;
}
