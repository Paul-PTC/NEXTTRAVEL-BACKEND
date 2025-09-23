package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_ESTADO_VIAJE")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwEstadoViaje {

    @Id
    @Column(name = "IDESTADOVIAJE")
    private Long idEstadoViaje;

    @Column(name = "IDRESERVA")
    private Long idReserva;

    @Column(name = "CLIENTE")
    private String cliente;      // nombreUsuario

    @Column(name = "LUGAR")
    private String lugar;        // nombreLugar

    @Column(name = "ESTADO")
    private String estado;       // nombreEstado

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
