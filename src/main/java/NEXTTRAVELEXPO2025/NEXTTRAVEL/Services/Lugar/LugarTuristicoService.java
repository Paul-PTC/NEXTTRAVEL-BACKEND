package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.LugarTuristicoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarTuristicoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LugarTuristicoService {

    private final LugarTuristicoRepository repo;

    // ===== Helpers =====
    private LugarTuristicoDTO toDTO(LugarTuristico e) {
        return LugarTuristicoDTO.builder()
                .nombreLugar(e.getNombreLugar())
                .descripcion(e.getDescripcion())
                .ubicacion(e.getUbicacion())
                .tipo(e.getTipo())
                .build();
    }

    private void apply(LugarTuristico e, LugarTuristicoDTO dto) {
        if (dto.getNombreLugar() != null) e.setNombreLugar(dto.getNombreLugar().trim());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getUbicacion() != null) e.setUbicacion(dto.getUbicacion());
        if (dto.getTipo() != null) e.setTipo(dto.getTipo());
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    // ===== Listado / Búsquedas =====
    public Page<LugarTuristicoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorNombre(String q, Pageable p) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro de búsqueda 'nombre' no puede estar vacío.");
        }
        return repo.findByNombreLugarContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorUbicacion(String q, Pageable p) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro de búsqueda 'ubicación' no puede estar vacío.");
        }
        return repo.findByUbicacionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorTipo(String q, Pageable p) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro de búsqueda 'tipo' no puede estar vacío.");
        }
        return repo.findByTipoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorDescripcion(String q, Pageable p) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro de búsqueda 'descripción' no puede estar vacío.");
        }
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public LugarTuristicoDTO crear(@Valid LugarTuristicoDTO dto) {
        if (!notBlank(dto.getNombreLugar())) {
            throw new BadRequestException("El campo 'nombreLugar' es obligatorio.");
        }

        if (repo.existsByNombreLugarIgnoreCase(dto.getNombreLugar())) {
            throw new ConflictException("El nombreLugar ya existe: " + dto.getNombreLugar());
        }

        try {
            LugarTuristico e = LugarTuristico.builder()
                    .nombreLugar(dto.getNombreLugar().trim())
                    .descripcion(dto.getDescripcion())
                    .ubicacion(dto.getUbicacion())
                    .tipo(dto.getTipo())
                    .build();

            LugarTuristico guardado = repo.save(e);
            log.info("LugarTuristico creado: {}", guardado.getNombreLugar());
            return toDTO(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear LugarTuristico: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public LugarTuristicoDTO actualizarPorId(Long id, @Valid LugarTuristicoDTO dto) {
        LugarTuristico e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró LugarTuristico con id: " + id));

        if (notBlank(dto.getNombreLugar())
                && !dto.getNombreLugar().equalsIgnoreCase(e.getNombreLugar())) {
            if (repo.existsByNombreLugarIgnoreCase(dto.getNombreLugar())) {
                throw new ConflictException("El nuevo nombreLugar ya existe: " + dto.getNombreLugar());
            }
        }

        apply(e, dto);

        try {
            LugarTuristico actualizado = repo.save(e);
            log.info("LugarTuristico actualizado: {} (id={})", actualizado.getNombreLugar(), id);
            return toDTO(actualizado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar LugarTuristico: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("No existe LugarTuristico con id: " + id);
        }
        try {
            repo.deleteById(id);
            log.info("LugarTuristico eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el LugarTuristico con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}
