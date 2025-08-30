package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MovimientoPuntosDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{8}-\\d$", message = "Formato de DUI inválido (########-#)")
    private String duiCliente;

    // opcional
    private Long idReserva;

    @NotBlank
    @Pattern(regexp = "^(ACREDITACION|CONSUMO)$", message = "tipo debe ser ACREDITACION o CONSUMO")
    private String tipo;

    @NotNull
    @Min(value = 1, message = "puntosCambiados debe ser > 0")
    private Integer puntosCambiados;

    @Size(max = 200)
    private String descripcion;

    // opcional; si viene null al crear, el service usa ahora()
    private LocalDateTime fecha;
}
