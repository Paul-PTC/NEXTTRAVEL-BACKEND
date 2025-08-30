package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PromocionDTO {

    private Long idPromocion; // lectura

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 200)
    private String descripcion;

    // null o entero 10..99
    @Min(value = 10, message = "descuentoPorcentaje debe ser 10..99")
    @Max(value = 99, message = "descuentoPorcentaje debe ser 10..99")
    private Integer descuentoPorcentaje;

    // pueden ser null (promos abiertas), pero si ambos existen: fin >= inicio
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
