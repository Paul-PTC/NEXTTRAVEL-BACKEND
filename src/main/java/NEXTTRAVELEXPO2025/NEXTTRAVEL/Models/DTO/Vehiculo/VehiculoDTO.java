package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehiculoDTO {

    private Long idVehiculo; // lectura

    @NotBlank @Size(max = 20)
    private String placa;

    @NotBlank @Size(max = 100)
    private String modelo;

    @NotNull @Min(1)
    private Integer capacidad;

    @Min(1900) @Max(2100)
    private Integer anioFabricacion; // opcional

    @Size(max = 50)
    private String estado; // opcional (si no viene, DB pone 'Activo')
}
