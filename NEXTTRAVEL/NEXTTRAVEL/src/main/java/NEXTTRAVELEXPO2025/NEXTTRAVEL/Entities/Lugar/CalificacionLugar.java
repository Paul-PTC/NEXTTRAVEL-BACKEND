package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CALIFICACIONLUGAR")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalificacionLugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCALIFICACIONLUGAR")
    private Long idCalificacionLugar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDLUGAR", nullable = false)
    private LugarTuristico lugar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DUICLIENTE", referencedColumnName = "DUI", nullable = false)
    private Cliente cliente;

    @Column(name = "PUNTUACION", nullable = false)
    private Integer puntuacion; // 1..5

    @Column(name = "COMENTARIO", length = 300)
    private String comentario;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP (si viene null, lo seteamos a ahora())
}
