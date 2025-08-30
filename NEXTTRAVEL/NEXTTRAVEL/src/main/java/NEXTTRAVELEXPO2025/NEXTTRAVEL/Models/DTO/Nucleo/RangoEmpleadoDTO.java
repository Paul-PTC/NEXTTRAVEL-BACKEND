package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RangoEmpleadoDTO {

    @NotBlank @Size(max = 100)
    private String nombreRango;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true, message = "salarioBase debe ser >= 0.00")
    private BigDecimal salarioBase;
}
