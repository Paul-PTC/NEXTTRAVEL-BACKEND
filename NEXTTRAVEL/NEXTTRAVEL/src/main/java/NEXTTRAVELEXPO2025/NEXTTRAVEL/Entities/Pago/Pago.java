package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPAGO")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRESERVA", nullable = false)
    private Reserva reserva;

    @Column(name = "MONTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "METODO", length = 50)
    private String metodo;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP (si viene null, lo seteamos en service)
}
