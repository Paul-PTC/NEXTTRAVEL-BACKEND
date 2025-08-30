package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_GANANCIAS")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwGanancia {

    @Id
    @Column(name = "IDGANANCIA")
    private Long idGanancia;

    @Column(name = "IDRESERVA")
    private Long idReserva;

    @Column(name = "CLIENTE")
    private String cliente; // nombreUsuario

    @Column(name = "LUGAR")
    private String lugar;   // nombreLugar

    @Column(name = "MONTO_BRUTO")
    private BigDecimal montoBruto;

    @Column(name = "MONTO_NETO")
    private BigDecimal montoNeto;

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
