package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLEPAGO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetallePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDDETALLEPAGO")
    private Long idDetallePago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDPAGO", nullable = false)
    private Pago pago;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "MONTO", precision = 10, scale = 2)
    private BigDecimal monto; // puede ser null; si viene, validamos >= 0
}
