package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GananciaDTO {

    @NotNull(message = "idReserva es obligatorio")
    private Long idReserva;

    @NotNull @DecimalMin(value = "0.00", message = "montoBruto debe ser >= 0")
    private BigDecimal montoBruto;

    @NotNull @DecimalMin(value = "0.00", message = "montoNeto debe ser >= 0")
    private BigDecimal montoNeto;

    // opcional; si viene null al crear, se usa ahora()
    private LocalDateTime fecha;
}

