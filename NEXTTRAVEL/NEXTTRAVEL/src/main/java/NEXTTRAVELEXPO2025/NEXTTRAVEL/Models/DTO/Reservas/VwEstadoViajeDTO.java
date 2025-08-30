package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwEstadoViajeDTO {
    private Long idEstadoViaje;
    private Long idReserva;
    private String cliente;
    private String lugar;
    private String estado;
    private LocalDateTime fecha;
}
