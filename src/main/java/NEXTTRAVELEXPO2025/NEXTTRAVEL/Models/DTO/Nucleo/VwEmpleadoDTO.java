package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwEmpleadoDTO {
    private String dui;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private String rango;
    private BigDecimal salarioBase;
    private LocalDateTime fechaContratacion;
}
