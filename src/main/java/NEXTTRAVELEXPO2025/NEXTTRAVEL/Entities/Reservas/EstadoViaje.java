package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Estado;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ESTADOVIAJE") // agrega schema si aplica: schema="\"C#NEXTTRAVEL\""
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDESTADOVIAJE")
    private Long idEstadoViaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRESERVA", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDESTADO", nullable = false)
    private Estado estado;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP
}
