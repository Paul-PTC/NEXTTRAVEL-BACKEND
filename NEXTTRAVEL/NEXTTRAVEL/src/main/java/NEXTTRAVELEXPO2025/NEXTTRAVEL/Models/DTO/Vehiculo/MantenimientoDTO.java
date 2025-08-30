package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MantenimientoDTO {

    @NotNull(message = "idVehiculo es obligatorio")
    private Long idVehiculo;

    @NotNull(message = "idTipoMantenimiento es obligatorio")
    private Long idTipoMantenimiento;

    @Size(max = 200)
    private String descripcion;

    // opcional; si viene null al crear, se usa ahora()
    private LocalDateTime fecha;
}
