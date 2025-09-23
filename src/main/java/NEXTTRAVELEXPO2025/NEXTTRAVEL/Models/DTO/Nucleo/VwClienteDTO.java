package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwClienteDTO {
    private String dui;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaRegistro;
    private Integer puntosActuales;
    private Long idUsuario;
}

