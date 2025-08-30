package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "VW_CLIENTES") // si usas esquema con #, agrega: schema="\"C#NEXTTRAVEL\""
@Immutable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VwCliente {

    @Id
    @Column(name = "DUI", length = 10, nullable = false)
    private String dui;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "CORREO")
    private String correo;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "FECHAREGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "PUNTOS_ACTUALES")
    private Integer puntosActuales; // NUMBER -> Integer
}
