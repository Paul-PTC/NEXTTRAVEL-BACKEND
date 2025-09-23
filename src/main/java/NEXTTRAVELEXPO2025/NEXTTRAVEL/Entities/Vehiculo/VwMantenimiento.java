package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "VW_MANTENIMIENTOS")
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwMantenimiento {

    @Id
    @Column(name = "IDMANTENIMIENTO")
    private Long idMantenimiento;

    @Column(name = "PLACA")
    private String placa;

    @Column(name = "MODELO")
    private String modelo;

    @Column(name = "TIPO_MANTENIMIENTO")
    private String tipoMantenimiento; // nombreTipo

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA")
    private LocalDateTime fecha;
}
