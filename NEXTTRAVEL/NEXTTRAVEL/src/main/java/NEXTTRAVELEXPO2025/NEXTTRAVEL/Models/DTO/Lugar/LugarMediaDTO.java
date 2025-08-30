package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LugarMediaDTO {

    @NotNull(message = "idLugar es obligatorio")
    private Long idLugar;

    @NotBlank
    @Size(max = 600)
    @Pattern(regexp = "^https?://.*", message = "url debe iniciar con http:// o https://")
    private String url;

    @Size(max = 200)
    private String altText;

    // si viene null, se asume false -> 'N'
    private Boolean primary;

    @Min(value = 1, message = "position debe ser >= 1")
    private Integer position;
}