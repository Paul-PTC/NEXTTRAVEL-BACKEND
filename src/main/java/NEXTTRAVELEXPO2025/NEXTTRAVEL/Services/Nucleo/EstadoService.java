package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Estado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EstadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.EstadoRepository;
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
public class EstadoService {

    private final EstadoRepository repo;

    // ===== Helpers =====
    private EstadoDTO toDTO(Estado e) {
        return EstadoDTO.builder()
                .nombreEstado(e.getNombreEstado())
                .build();
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    // ===== Listado / Búsquedas =====
    public Page<EstadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<EstadoDTO> buscarPorNombre(String q, Pageable pageable) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro 'nombreEstado' no puede estar vacío.");
        }
        return repo.findByNombreEstadoContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public EstadoDTO crear(@Valid EstadoDTO dto) {
        if (!notBlank(dto.getNombreEstado())) {
            throw new BadRequestException("El campo 'nombreEstado' es obligatorio.");
        }

        if (repo.existsByNombreEstadoIgnoreCase(dto.getNombreEstado())) {
            throw new ConflictException("El nombreEstado ya existe: " + dto.getNombreEstado());
        }

        try {
            Estado e = Estado.builder()
                    .nombreEstado(dto.getNombreEstado().trim())
                    .build();

            Estado guardado = repo.save(e);
            log.info("Estado creado: {}", guardado.getNombreEstado());
            return toDTO(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Estado: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public EstadoDTO actualizarPorId(Long id, @Valid EstadoDTO dto) {
        Estado e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Estado con id: " + id));

        if (notBlank(dto.getNombreEstado())
                && !dto.getNombreEstado().equalsIgnoreCase(e.getNombreEstado())) {
            if (repo.existsByNombreEstadoIgnoreCase(dto.getNombreEstado())) {
                throw new ConflictException("El nuevo nombreEstado ya existe: " + dto.getNombreEstado());
            }
            e.setNombreEstado(dto.getNombreEstado().trim());
        }

        try {
            Estado actualizado = repo.save(e);
            log.info("Estado actualizado: {} (id={})", actualizado.getNombreEstado(), id);
            return toDTO(actualizado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Estado: "
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
            throw new ResourceNotFoundException("No existe Estado con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("Estado eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el Estado con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}
