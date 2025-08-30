package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GastoDTO {
    @NotNull(message = "idTipoGasto es obligatorio")
    private Long idTipoGasto;

    @NotNull @DecimalMin(value = "0.00", message = "monto debe ser >= 0")
    private BigDecimal monto;

    @Size(max = 200)
    private String descripcion;

    // opcional; si viene null al crear, se usa ahora()
    private LocalDateTime fecha;
}
