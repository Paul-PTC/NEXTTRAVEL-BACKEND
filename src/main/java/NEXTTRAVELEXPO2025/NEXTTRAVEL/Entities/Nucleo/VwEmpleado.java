package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_EMPLEADOS") // opcional: schema="\"C#NEXTTRAVEL\""
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwEmpleado {

    @Id
    @Column(name = "DUI", length = 10, nullable = false)
    private String dui;

    @Column(name = "NOMBRECOMPLETO")
    private String nombre;

    @Column(name = "CORREO")
    private String correo;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "RANGO")
    private String rango;

    @Column(name = "SALARIO_BASE", precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "FECHACONTRATACION")
    private LocalDateTime fechaContratacion;
}
