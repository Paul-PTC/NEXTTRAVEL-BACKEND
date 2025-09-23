package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VEHICULO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDVEHICULO")
    private Long idVehiculo;

    @Column(name = "PLACA", nullable = false, unique = true, length = 20)
    private String placa;

    @Column(name = "MODELO", nullable = false, length = 100)
    private String modelo;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @Column(name = "ANIOFABRICACION")
    private Integer anioFabricacion;

    @Column(name = "ESTADO", length = 50)
    private String estado; // DEFAULT 'Activo' (lo respetamos si viene null)
}
