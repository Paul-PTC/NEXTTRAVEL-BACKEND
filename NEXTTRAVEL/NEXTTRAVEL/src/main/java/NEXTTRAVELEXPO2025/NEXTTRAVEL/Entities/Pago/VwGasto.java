package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_GASTOS")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwGasto {

    @Id
    @Column(name = "IDGASTO")
    private Long idGasto;

    @Column(name = "TIPO_GASTO")
    private String tipoGasto; // nombreTipo

    @Column(name = "MONTO")
    private BigDecimal monto;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
