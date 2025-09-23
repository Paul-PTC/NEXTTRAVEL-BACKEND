package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GANANCIA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ganancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDGANANCIA")
    private Long idGanancia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRESERVA", nullable = false, unique = true)
    private Reserva reserva;

    @Column(name = "MONTO_BRUTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "MONTO_NETO", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoNeto;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP
}
