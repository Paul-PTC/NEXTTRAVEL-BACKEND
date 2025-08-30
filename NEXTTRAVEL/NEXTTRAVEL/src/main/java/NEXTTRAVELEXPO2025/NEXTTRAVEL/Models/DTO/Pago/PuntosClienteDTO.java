package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PuntosClienteDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{8}-\\d$", message = "Formato de DUI inválido (########-#)")
    private String duiCliente;

    @NotNull
    @Min(value = 0, message = "puntos debe ser >= 0")
    private Integer puntos;
}
