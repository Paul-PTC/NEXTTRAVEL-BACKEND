package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_CALIFICACIONES_LUGAR")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwCalificacionLugar {

    @Id
    @Column(name = "IDCALIFICACIONLUGAR")
    private Long idCalificacionLugar;

    @Column(name = "NOMBRELUGAR")
    private String nombreLugar;

    @Column(name = "CLIENTE")
    private String cliente; // nombreUsuario del cliente

    @Column(name = "PUNTUACION")
    private Integer puntuacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
