package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Seguridad;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodigoVerificacionDTO {

    private Long idCodigo; // lectura

    @NotNull(message = "idUsuario es obligatorio")
    private Long idUsuario;

    @NotBlank @Size(max = 10, message = "codigo máximo 10 caracteres")
    private String codigo;

    // opcionales; si no vienen al crear, se setean por defecto en Service
    private LocalDateTime fechaGeneracion;
    private LocalDate validoHasta;
}
