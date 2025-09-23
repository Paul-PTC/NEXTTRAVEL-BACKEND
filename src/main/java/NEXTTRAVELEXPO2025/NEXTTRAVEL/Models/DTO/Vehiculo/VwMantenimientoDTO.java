package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwMantenimientoDTO {
    private Long idMantenimiento;
    private String placa;
    private String modelo;
    private String tipoMantenimiento; // nombre del tipo
    private String descripcion;
    private LocalDateTime fecha;
}
