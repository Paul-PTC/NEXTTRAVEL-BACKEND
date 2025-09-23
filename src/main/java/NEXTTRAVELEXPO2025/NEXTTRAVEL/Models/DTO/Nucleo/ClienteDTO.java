package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteDTO {
    @NotBlank
    @Pattern(regexp = "^\\d{8}-\\d$", message = "Formato de DUI inválido (########-#)")
    private String dui;

    @NotNull(message = "idUsuario es obligatorio")
    private Long idUsuario;

    @Size(max = 15)
    private String telefono;

    @Size(max = 200)
    private String direccion;

    // si viene null al crear, se setea ahora()
    private LocalDateTime fechaRegistro;

    private Long puntosactuales;
}
