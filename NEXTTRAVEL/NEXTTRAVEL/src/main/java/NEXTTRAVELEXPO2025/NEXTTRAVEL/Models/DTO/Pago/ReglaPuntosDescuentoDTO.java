package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReglaPuntosDescuentoDTO {

    private Long idRegla; // solo lectura

    @NotNull @Min(value = 1, message = "puntosRequeridos debe ser > 0")
    private Integer puntosRequeridos;

    @NotNull
    @Min(value = 10, message = "porcentajeDescuento debe ser entre 10 y 99")
    @Max(value = 99, message = "porcentajeDescuento debe ser entre 10 y 99")
    private Integer porcentajeDescuento; // entero
}
