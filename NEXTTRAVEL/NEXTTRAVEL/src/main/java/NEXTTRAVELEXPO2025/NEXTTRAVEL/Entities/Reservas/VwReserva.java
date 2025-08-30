package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_RESERVAS")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwReserva {

    @Id
    @Column(name = "IDRESERVA")
    private Long idReserva;

    @Column(name = "DUI_CLIENTE")
    private String duiCliente;

    @Column(name = "NOMBRE_CLIENTE")
    private String nombreCliente;

    @Column(name = "LUGAR")
    private String lugar;

    @Column(name = "FECHARESERVA")
    private LocalDateTime fechaReserva;

    @Column(name = "CANTIDADPERSONAS")
    private Integer cantidadPersonas;

    @Column(name = "ESTADO_ACTUAL")
    private String estadoActual; // 'Pendiente' si no hay estado en EV
}
