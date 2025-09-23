package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TIPOGASTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTIPOGASTO")
    private Long idTipoGasto;

    @Column(name = "NOMBRETIPO", nullable = false, unique = true, length = 100)
    private String nombreTipo;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
}
