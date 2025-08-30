package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLEADO") // agrega schema si lo necesitas: schema="\"C#NEXTTRAVEL\""
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Empleado {

    @Id
    @Column(name = "DUI", length = 10, nullable = false)
    private String dui;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDUSUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDRANGO", nullable = false)
    private RangoEmpleado rango;

    @Column(name = "TELEFONO", length = 15, unique = true)
    private String telefono;

    @Column(name = "DIRECCION", length = 200)
    private String direccion;

    @Column(name = "FECHACONTRATACION")
    private LocalDateTime fechaContratacion;
}
