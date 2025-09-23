package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REGLAPUNTOSDESCUENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReglaPuntosDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDREGLA")
    private Long idRegla;

    @Column(name = "PUNTOS_REQUERIDOS", nullable = false, unique = true)
    private Integer puntosRequeridos; // > 0

    // En DB es NUMBER(5,2) pero con TRUNC obliga enteros; lo manejamos como Integer
    @Column(name = "PORCENTAJE_DESCUENTO", nullable = false, unique = true)
    private Integer porcentajeDescuento; // 10..99 (enteros)
}
