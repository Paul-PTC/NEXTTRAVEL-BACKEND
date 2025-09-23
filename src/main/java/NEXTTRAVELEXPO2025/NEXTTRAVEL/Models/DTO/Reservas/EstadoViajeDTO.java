package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoViajeDTO {

    @NotNull(message = "idReserva es obligatorio")
    private Long idReserva;

    @NotNull(message = "idEstado es obligatorio")
    private Long idEstado;

    // opcional; si viene null al crear, el service usa ahora()
    private LocalDateTime fecha;
}
