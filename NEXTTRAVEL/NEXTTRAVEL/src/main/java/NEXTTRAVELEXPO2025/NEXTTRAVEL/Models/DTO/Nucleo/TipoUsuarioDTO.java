package NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TipoUsuarioDTO {

    private Long id;
    private String nombreTipo;
    private String descripcion;
}