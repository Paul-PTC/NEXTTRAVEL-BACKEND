package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.TipoUsuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.UsuarioDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.TipoUsuarioRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;
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

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    // ===== Listado / Búsquedas =====
    public Page<UsuarioDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorNombre(String q, Pageable pageable) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro 'nombreUsuario' no puede estar vacío.");
        }
        return repo.findByNombreUsuarioContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorCorreo(String q, Pageable pageable) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro 'correo' no puede estar vacío.");
        }
        return repo.findByCorreoContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    public Page<UsuarioDTO> buscarPorRol(String rol, Pageable pageable) {
        if (!notBlank(rol)) {
            throw new BadRequestException("El parámetro 'rol' no puede estar vacío.");
        }
        return repo.findByRolIgnoreCase(rol, pageable).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (!notBlank(dto.getNombreUsuario())) {
            throw new BadRequestException("El campo 'nombreUsuario' es obligatorio.");
        }
        if (!notBlank(dto.getCorreo())) {
            throw new BadRequestException("El campo 'correo' es obligatorio.");
        }
        if (!notBlank(dto.getRol())) {
            throw new BadRequestException("El campo 'rol' es obligatorio.");
        }
        if (!notBlank(dto.getPassword())) {
            throw new BadRequestException("El campo 'password' es obligatorio.");
        }

        if (repo.existsByNombreUsuarioIgnoreCase(dto.getNombreUsuario())) {
            throw new ConflictException("El nombre de usuario ya existe: " + dto.getNombreUsuario());
        }
        if (repo.existsByCorreoIgnoreCase(dto.getCorreo())) {
            throw new ConflictException("El correo ya está registrado: " + dto.getCorreo());
        }

        // Obtener TipoUsuario desde el repositorio
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(dto.getIdTipoUsuario())
                .orElseThrow(() -> new BadRequestException("Tipo de usuario no válido con ID: " + dto.getIdTipoUsuario()));

        try {
            Usuario u = Usuario.builder()
                    .nombreUsuario(dto.getNombreUsuario())
                    .correo(dto.getCorreo())
                    .idUsuario(dto.getIdUsuario())
                    .tipoUsuario(tipoUsuario)
                    .contraseniaHash(passwordEncoder.encode(dto.getPassword()))
                    .build();

            Usuario guardado = repo.save(u);
            log.info("Usuario creado: {}", guardado.getNombreUsuario());
            return toDTO(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Usuario: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public UsuarioDTO actualizarPorId(Long id, UsuarioDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        Usuario u = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró usuario con id: " + id));

        if (notBlank(dto.getNombreUsuario())
                && !dto.getNombreUsuario().equalsIgnoreCase(u.getNombreUsuario())) {
            if (repo.existsByNombreUsuarioIgnoreCase(dto.getNombreUsuario())) {
                throw new ConflictException("El nuevo nombre de usuario ya existe: " + dto.getNombreUsuario());
            }
            u.setNombreUsuario(dto.getNombreUsuario());
        }

        if (notBlank(dto.getCorreo())
                && !dto.getCorreo().equalsIgnoreCase(u.getCorreo())) {
            if (repo.existsByCorreoIgnoreCase(dto.getCorreo())) {
                throw new ConflictException("El nuevo correo ya está registrado: " + dto.getCorreo());
            }
            u.setCorreo(dto.getCorreo());
        }

        if (notBlank(dto.getRol())) {
            u.setRol(dto.getRol());
        }

        if (notBlank(dto.getPassword())) {
            u.setContraseniaHash(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getIdTipoUsuario() != null && !dto.getIdTipoUsuario().equals(u.getTipoUsuario().getId())) {
            TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(dto.getIdTipoUsuario())
                    .orElseThrow(() -> new BadRequestException("Tipo de usuario no válido con ID: " + dto.getIdTipoUsuario()));
            u.setTipoUsuario(tipoUsuario);
        }

        try {
            Usuario actualizado = repo.save(u);
            log.info("Usuario actualizado: {} (id={})", actualizado.getNombreUsuario(), id);
            return toDTO(actualizado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Usuario: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }


    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("No existe Usuario con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("Usuario eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el Usuario con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}