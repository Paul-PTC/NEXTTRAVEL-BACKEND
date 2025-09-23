package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalificacionLugarDTO {

    @NotNull(message = "idLugar es obligatorio")
    private Long idLugar;

    @NotBlank(message = "duiCliente es obligatorio")
    @Pattern(regexp = "^\\d{8}-\\d$", message = "Formato de DUI inválido (########-#)")
    private String duiCliente;

    @NotNull
    @Min(value = 1) @Max(value = 5)
    private Integer puntuacion;

    @Size(max = 300)
    private String comentario;

    // opcional; si es null en crear, el service usa ahora()
    private LocalDateTime fecha;
}
