package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DescuentoAplicadoDTO {

    @NotNull(message = "idReserva es obligatorio")
    private Long idReserva;

    @NotBlank
    @Pattern(regexp = "^(PUNTOS|PROMOCION|MANUAL)$", message = "tipoDescuento debe ser PUNTOS, PROMOCION o MANUAL")
    private String tipoDescuento;

    // SOLO PROMOCION
    private Long idPromocion;

    // SOLO PUNTOS
    private Long idRegla;
    @Min(value = 1, message = "puntosUsados debe ser > 0")
    private Integer puntosUsados;

    // null para PUNTOS/PROMOCION (se deriva), obligatorio para MANUAL
    @Min(10) @Max(99)
    private Integer porcentajeAplicado;

    private LocalDateTime fecha; // opcional
}
