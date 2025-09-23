package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetallePagoDTO {

    @NotNull(message = "idPago es obligatorio")
    private Long idPago;

    @Size(max = 200, message = "descripcion máximo 200 caracteres")
    private String descripcion;

    // opcional; si viene, el service valida que sea >= 0
    private BigDecimal monto;
}
