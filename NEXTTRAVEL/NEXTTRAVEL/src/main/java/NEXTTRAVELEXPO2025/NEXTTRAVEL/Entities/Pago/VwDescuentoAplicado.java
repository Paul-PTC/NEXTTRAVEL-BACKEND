package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_DESCUENTOS_APLICADOS")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwDescuentoAplicado {

    @Id
    @Column(name = "IDDESCUENTOAPLICADO")
    private Long idDescuentoAplicado;

    @Column(name = "IDRESERVA")
    private Long idReserva;

    @Column(name = "CLIENTE")
    private String cliente; // nombreUsuario

    @Column(name = "LUGAR")
    private String lugar;   // nombreLugar

    @Column(name = "TIPO_DESCUENTO")
    private String tipoDescuento;

    @Column(name = "PROMOCION")
    private String promocion; // nombre de la promo (puede ser null si no aplica)

    @Column(name = "PUNTOS_USADOS")
    private Integer puntosUsados;

    @Column(name = "PORCENTAJE_APLICADO")
    private Integer porcentajeAplicado;

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
