package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOVIMIENTOPUNTOS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MovimientoPuntos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMOVIMIENTO")
    private Long idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DUICLIENTE", referencedColumnName = "DUI", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDRESERVA")
    private Reserva reserva; // opcional

    @Column(name = "TIPO", nullable = false, length = 20)
    private String tipo; // 'ACREDITACION' | 'CONSUMO'

    @Column(name = "PUNTOS_CAMBIADOS", nullable = false)
    private Integer puntosCambiados; // > 0

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP
}
