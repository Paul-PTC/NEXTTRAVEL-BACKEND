package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwGastoDTO {
    private Long idGasto;
    private String tipoGasto; // nombre del tipo
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;
}
