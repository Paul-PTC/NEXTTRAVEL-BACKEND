package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LugarTuristicoDTO {

    @NotBlank @Size(max = 150)
    private String nombreLugar;

    @Size(max = 300)
    private String descripcion;

    @Size(max = 200)
    private String ubicacion;

    @Size(max = 100)
    private String tipo;
}
