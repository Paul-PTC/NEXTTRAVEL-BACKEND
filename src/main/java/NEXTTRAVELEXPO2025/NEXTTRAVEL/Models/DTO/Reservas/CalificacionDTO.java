package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalificacionDTO {

    @NotNull(message = "idReserva es obligatorio")
    private Long idReserva;

    @NotNull @Min(1) @Max(5)
    private Integer puntuacion;

    @Size(max = 300)
    private String comentario;

    // opcional; si viene null al crear, se usa ahora()
    private LocalDateTime fecha;
}
