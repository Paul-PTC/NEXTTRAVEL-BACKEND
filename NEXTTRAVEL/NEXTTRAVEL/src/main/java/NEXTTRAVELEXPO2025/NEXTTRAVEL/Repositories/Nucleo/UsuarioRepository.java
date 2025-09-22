package NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo;


import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findByNombreUsuarioContainingIgnoreCase(String q, Pageable pageable);

    Page<Usuario> findByCorreoContainingIgnoreCase(String q, Pageable pageable);

    Page<Usuario> findByRolIgnoreCase(String rol, Pageable pageable);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByCorreo(String email);
}
