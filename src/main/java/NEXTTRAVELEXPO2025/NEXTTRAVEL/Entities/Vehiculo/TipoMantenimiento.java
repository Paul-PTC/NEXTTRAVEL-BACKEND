package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPOMANTENIMIENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTIPOMANTENIMIENTO")
    private Long idTipoMantenimiento;

    @Column(name = "NOMBRETIPO", nullable = false, unique = true, length = 100)
    private String nombreTipo;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
}
