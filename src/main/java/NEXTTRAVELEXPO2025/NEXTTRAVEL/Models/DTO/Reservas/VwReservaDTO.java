package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwReservaDTO {
    private Long idReserva;
    private String duiCliente;
    private String nombreCliente;
    private String lugar;
    private LocalDateTime fechaReserva;
    private Integer cantidadPersonas;
    private String estadoActual;
}