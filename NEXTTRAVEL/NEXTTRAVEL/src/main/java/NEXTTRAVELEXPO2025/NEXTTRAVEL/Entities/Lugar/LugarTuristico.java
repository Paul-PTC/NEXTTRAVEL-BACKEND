package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LUGARTURISTICO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LugarTuristico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLUGAR")
    private Long idLugar;

    @Column(name = "NOMBRELUGAR", nullable = false, length = 150, unique = true)
    private String nombreLugar;

    @Column(name = "DESCRIPCION", length = 300)
    private String descripcion;

    @Column(name = "UBICACION", length = 200)
    private String ubicacion;

    @Column(name = "TIPO", length = 100)
    private String tipo;
}
