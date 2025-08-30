package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioDTO {

    @NotBlank @Size(max = 100)
    private String nombreUsuario;

    @NotBlank @Email @Size(max = 150)
    private String correo;

    @NotBlank @Size(max = 50)
    private String rol;

    // Solo escritura (no se devuelve)
    @Size(min = 6, max = 200)
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
}
