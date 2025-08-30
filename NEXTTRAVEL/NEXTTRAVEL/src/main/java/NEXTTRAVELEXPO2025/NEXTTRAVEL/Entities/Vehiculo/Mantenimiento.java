package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MANTENIMIENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMANTENIMIENTO")
    private Long idMantenimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDVEHICULO", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDTIPOMANTENIMIENTO", nullable = false)
    private TipoMantenimiento tipoMantenimiento;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
}
