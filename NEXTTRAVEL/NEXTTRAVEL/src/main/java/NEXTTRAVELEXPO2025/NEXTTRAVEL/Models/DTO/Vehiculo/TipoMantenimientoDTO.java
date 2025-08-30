package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoMantenimientoDTO {

    private Long idTipoMantenimiento; // lectura

    @NotBlank
    @Size(max = 100)
    private String nombreTipo;

    @Size(max = 200)
    private String descripcion;
}
