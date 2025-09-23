package NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USUARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUSUARIO")
    private Long idUsuario;

    @Column(name = "NOMBREUSUARIO", nullable = false, length = 100, unique = true)
    private String nombreUsuario;

    @Column(name = "CONTRASENIA_HASH", nullable = false, length = 200)
    private String contraseniaHash;

    @Column(name = "CORREO", nullable = false, length = 150, unique = true)
    private String correo;

    @Column(name = "ROL", nullable = false, length = 50)
    private String rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTIPOUSUARIO", referencedColumnName = "IDTIPOUSUARIO")
    private TipoUsuario tipoUsuario;

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", contraseniaHash='" + contraseniaHash + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                '}';
    }

}
