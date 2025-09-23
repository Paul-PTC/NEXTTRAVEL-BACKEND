package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwCalificacionLugarDTO {
    private Long idCalificacionLugar;
    private String nombreLugar;
    private String cliente;     // nombreUsuario
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
}
