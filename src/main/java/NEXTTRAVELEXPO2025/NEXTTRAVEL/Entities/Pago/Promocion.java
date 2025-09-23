package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "PROMOCION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROMOCION")
    private Long idPromocion;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    // En Oracle es NUMBER(5,2) con TRUNC -> forzamos entero en Java; puede ser null
    @Column(name = "DESCUENTOPORCENTAJE")
    private Integer descuentoPorcentaje;

    @Column(name = "FECHAINICIO")
    private LocalDate fechaInicio;  // DATE

    @Column(name = "FECHAFIN")
    private LocalDate fechaFin;     // DATE
}
