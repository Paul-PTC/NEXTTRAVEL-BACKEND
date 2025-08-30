package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLEMANTENIMIENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDDETALLEMANTENIMIENTO")
    private Long idDetalleMantenimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDMANTENIMIENTO", nullable = false)
    private Mantenimiento mantenimiento;

    @Column(name = "ACTIVIDAD", nullable = false, length = 200)
    private String actividad;

    @Column(name = "COSTO", precision = 10, scale = 2)
    private BigDecimal costo; // opcional
}
