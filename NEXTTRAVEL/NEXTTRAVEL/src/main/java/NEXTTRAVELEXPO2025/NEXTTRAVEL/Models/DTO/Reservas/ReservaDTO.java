package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservaDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{8}-\\d$", message = "Formato de DUI inválido (########-#)")
    private String duiCliente;

    @NotNull(message = "idLugar es obligatorio")
    private Long idLugar;

    // si viene null en crear, se pone ahora()
    private LocalDateTime fechaReserva;

    @NotNull @Min(value = 1, message = "cantidadPersonas debe ser > 0")
    private Integer cantidadPersonas;

    @DecimalMin(value = "-90.0")  @DecimalMax(value = "90.0")
    private BigDecimal pickupLat;
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal pickupLng;

    @Size(max = 300)
    private String pickupAddress;

    @DecimalMin(value = "-90.0")  @DecimalMax(value = "90.0")
    private BigDecimal dropLat;
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal dropLng;

    @Size(max = 300)
    private String dropAddress;

    private LocalDateTime horaRecogida;
}
