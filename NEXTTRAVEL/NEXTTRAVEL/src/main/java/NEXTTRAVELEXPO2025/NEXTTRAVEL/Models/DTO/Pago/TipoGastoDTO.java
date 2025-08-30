package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoGastoDTO {

    private Long idTipoGasto; // lectura

    @NotBlank
    @Size(max = 100)
    private String nombreTipo;

    @Size(max = 200)
    private String descripcion;
}