package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PUNTOSCLIENTE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PuntosCliente {

    @Id
    @Column(name = "DUICLIENTE", length = 10, nullable = false)
    private String duiCliente; // PK y FK a CLIENTE.DUI

    @Column(name = "PUNTOS", nullable = false)
    private Integer puntos;

    @Column(name = "FECHAACTUALIZACION", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion; // DEFAULT SYSTIMESTAMP (lo deja la DB)
}
