package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLIENTE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @Column(name = "DUI", length = 10, nullable = false)
    private String dui;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDUSUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "TELEFONO", length = 15, unique = true)
    private String telefono;

    @Column(name = "DIRECCION", length = 200)
    private String direccion;

    @Column(name = "FECHAREGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "PUNTOS_ACTUALES")
    private Long puntosactuales;
}
