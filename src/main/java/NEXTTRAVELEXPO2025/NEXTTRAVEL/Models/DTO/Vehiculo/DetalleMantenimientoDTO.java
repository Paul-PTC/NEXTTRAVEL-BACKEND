package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleMantenimientoDTO {

    @NotNull(message = "idMantenimiento es obligatorio")
    private Long idMantenimiento;

    @NotBlank @Size(max = 200)
    private String actividad;

    // opcional; si viene, el service valida que sea >= 0
    private BigDecimal costo;
}
