package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DESCUENTOAPLICADO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DescuentoAplicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDDESCUENTOAPLICADO")
    private Long idDescuentoAplicado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRESERVA", nullable = false)
    private Reserva reserva;

    @Column(name = "TIPO_DESCUENTO", nullable = false, length = 20)
    private String tipoDescuento; // PUNTOS | PROMOCION | MANUAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDPROMOCION")
    private Promocion promocion; // solo PROMOCION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDREGLA")
    private ReglaPuntosDescuento regla; // solo PUNTOS

    @Column(name = "PUNTOS_USADOS")
    private Integer puntosUsados; // solo PUNTOS (>0)

    // En DB es NUMBER(5,2) con TRUNC -> lo manejamos como entero 10..99
    @Column(name = "PORCENTAJE_APLICADO", nullable = false)
    private Integer porcentajeAplicado;

    @Column(name = "FECHA")
    private LocalDateTime fecha; // DEFAULT SYSTIMESTAMP
}
