package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Seguridad;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CODIGOVERIFICACION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodigoVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCODIGO")
    private Long idCodigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDUSUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;

    @Column(name = "FECHAGENERACION")
    private LocalDateTime fechaGeneracion; // DEFAULT SYSTIMESTAMP

    @Column(name = "VALIDOHASTA")
    private LocalDate validoHasta; // DATE
}
