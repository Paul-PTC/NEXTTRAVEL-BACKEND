package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwDescuentoAplicadoDTO {
    private Long idDescuentoAplicado;
    private Long idReserva;
    private String cliente;
    private String lugar;
    private String tipoDescuento;
    private String promocion;
    private Integer puntosUsados;
    private Integer porcentajeAplicado;
    private LocalDateTime fecha;
}
