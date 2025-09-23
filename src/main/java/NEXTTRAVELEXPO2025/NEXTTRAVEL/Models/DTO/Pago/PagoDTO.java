package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoDTO {

    @NotNull(message = "idReserva es obligatorio")
    private Long idReserva;

    @NotNull @DecimalMin(value = "0.00", message = "monto debe ser >= 0")
    private BigDecimal monto;

    @Size(max = 50, message = "metodo máximo 50 caracteres")
    private String metodo;

    // opcional; si viene null al crear, se usa ahora()
    private LocalDateTime fecha;
}
