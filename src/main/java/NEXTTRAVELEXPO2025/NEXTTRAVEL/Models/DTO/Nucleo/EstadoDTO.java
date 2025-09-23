package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoDTO {

    @NotBlank
    @Size(max = 100)
    private String nombreEstado;
}