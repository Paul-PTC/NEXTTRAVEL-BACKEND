package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CALIFICACION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCALIFICACION")
    private Long idCalificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRESERVA", nullable = false)
    private Reserva reserva;

    @Column(name = "PUNTUACION", nullable = false)
    private Integer puntuacion; // 1..5

    @Column(name = "COMENTARIO", length = 300)
    private String comentario;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP
}
