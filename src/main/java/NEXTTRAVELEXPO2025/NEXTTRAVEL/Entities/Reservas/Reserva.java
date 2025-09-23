package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDRESERVA")
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DUICLIENTE", referencedColumnName = "DUI", nullable = false)
    private Cliente cliente; // FK a CLIENTE.DUI

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDLUGAR", nullable = false)
    private LugarTuristico lugar; // FK a LUGARTURISTICO.IDLUGAR

    @Column(name = "FECHARESERVA")
    private LocalDateTime fechaReserva;

    @Column(name = "CANTIDADPERSONAS", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "PICKUP_LAT", precision = 9, scale = 6)
    private BigDecimal pickupLat;

    @Column(name = "PICKUP_LNG", precision = 9, scale = 6)
    private BigDecimal pickupLng;

    @Column(name = "PICKUP_ADDRESS", length = 300)
    private String pickupAddress;

    @Column(name = "DROP_LAT", precision = 9, scale = 6)
    private BigDecimal dropLat;

    @Column(name = "DROP_LNG", precision = 9, scale = 6)
    private BigDecimal dropLng;

    @Column(name = "DROP_ADDRESS", length = 300)
    private String dropAddress;

    @Column(name = "HORARECOGIDA")
    private LocalDateTime horaRecogida;
}
