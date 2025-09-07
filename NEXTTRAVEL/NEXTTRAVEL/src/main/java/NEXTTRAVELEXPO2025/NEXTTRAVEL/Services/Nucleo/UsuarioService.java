package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.UsuarioDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    // ===== Helpers =====
    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .idUsuario(u.getIdUsuario())
                .nombreUsuario(u.getNombreUsuario())
                .correo(u.getCorreo())
                .rol(u.getRol())

                .build();
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    // ===== Listado / Búsquedas =====
    public Page<UsuarioDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorNombre(String q, Pageable pageable) {
        return repo.findByNombreUsuarioContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorCorreo(String q, Pageable pageable) {
        return repo.findByCorreoContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorRol(String rol, Pageable pageable) {
        return repo.findByRolIgnoreCase(rol, pageable).map(this::toDTO);
    }

    // ===== Crear =====
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (!notBlank(dto.getNombreUsuario()))
            throw new IllegalArgumentException("nombreUsuario es obligatorio.");
        if (!notBlank(dto.getCorreo()))
            throw new IllegalArgumentException("correo es obligatorio.");
        if (!notBlank(dto.getRol()))
            throw new IllegalArgumentException("rol es obligatorio.");
        if (!notBlank(dto.getPassword()))
            throw new IllegalArgumentException("password es obligatorio.");

        if (repo.existsByNombreUsuarioIgnoreCase(dto.getNombreUsuario()))
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        if (repo.existsByCorreoIgnoreCase(dto.getCorreo()))
            throw new IllegalArgumentException("El correo ya está registrado.");

        Usuario u = Usuario.builder()
                .nombreUsuario(dto.getNombreUsuario())
                .correo(dto.getCorreo())
                .rol(dto.getRol())
                .idUsuario(dto.getIdUsuario())
                .contraseniaHash(passwordEncoder.encode(dto.getPassword()))
                .build();

        Usuario guardado = repo.save(u);
        log.info("Usuario creado: {}", guardado.getNombreUsuario());
        return toDTO(guardado);
    }

    // ===== Actualizar por ID =====
    public UsuarioDTO actualizarPorId(Long id, UsuarioDTO dto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró usuario con id: " + id));

        if (notBlank(dto.getNombreUsuario())
                && !dto.getNombreUsuario().equalsIgnoreCase(u.getNombreUsuario())) {
            if (repo.existsByNombreUsuarioIgnoreCase(dto.getNombreUsuario()))
                throw new IllegalArgumentException("El nuevo nombre de usuario ya existe.");
            u.setNombreUsuario(dto.getNombreUsuario());
        }

        if (notBlank(dto.getCorreo())
                && !dto.getCorreo().equalsIgnoreCase(u.getCorreo())) {
            if (repo.existsByCorreoIgnoreCase(dto.getCorreo()))
                throw new IllegalArgumentException("El nuevo correo ya está registrado.");
            u.setCorreo(dto.getCorreo());
        }

        if (notBlank(dto.getRol())) {
            u.setRol(dto.getRol());
        }


        if (notBlank(dto.getPassword())) {
            u.setContraseniaHash(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario actualizado = repo.save(u);
        log.info("Usuario actualizado: {} (id={})", actualizado.getNombreUsuario(), id);
        return toDTO(actualizado);
    }

    // ===== Eliminar por ID =====
    public boolean eliminarPorId(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Usuario eliminado id={}", id);
        return true;
    }
}