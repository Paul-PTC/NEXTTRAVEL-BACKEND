package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwGananciaDTO {
    private Long idGanancia;
    private Long idReserva;
    private String cliente;
    private String lugar;
    private BigDecimal montoBruto;
    private BigDecimal montoNeto;
    private LocalDateTime fecha;
}
