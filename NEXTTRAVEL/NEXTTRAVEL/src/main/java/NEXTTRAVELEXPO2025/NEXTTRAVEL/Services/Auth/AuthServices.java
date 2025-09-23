package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Auth;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Config.Argon2Password;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServices {
    @Autowired
    private UsuarioRepository repo;

    public boolean Login(String correo, String contrasena){
        Argon2Password objHash = new Argon2Password();
        Optional<Usuario> list = repo.findByCorreo(correo).stream().findFirst();
        if (list.isPresent()){
            Usuario usuario = list.get();
            String nombreTipoUsuario = usuario.getTipoUsuario().getNombreTipo();
            System.out.println("Usuario encontrado ID: " + usuario.getIdUsuario() +
                    ", email: " + usuario.getCorreo() +
                    ", rol: " + nombreTipoUsuario);
            String HashDB = usuario.getContraseniaHash();
            boolean verificado = objHash.VerifyPassword(HashDB, contrasena);
            return verificado;
        }
        return false;
    }

    public Optional<Usuario> obtenerUsuario(String email){
        // Buscar usuario completo en la base de datos
        Optional<Usuario> userOpt = repo.findByCorreo(email);
        System.out.println("Se obtuvo el Usuario Completo");
        return (userOpt != null) ? userOpt : null;
    }
}
