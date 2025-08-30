package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Estado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EstadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.EstadoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    // ===== Listado / Búsquedas =====
    public Page<EstadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<EstadoDTO> buscarPorNombre(String q, Pageable pageable) {
        return repo.findByNombreEstadoContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public EstadoDTO crear(@Valid EstadoDTO dto) {
        if (!notBlank(dto.getNombreEstado()))
            throw new IllegalArgumentException("nombreEstado es obligatorio.");

        if (repo.existsByNombreEstadoIgnoreCase(dto.getNombreEstado()))
            throw new IllegalArgumentException("El nombreEstado ya existe.");

        Estado e = Estado.builder()
                .nombreEstado(dto.getNombreEstado().trim())
                .build();

        Estado guardado = repo.save(e);
        log.info("Estado creado: {}", guardado.getNombreEstado());
        return toDTO(guardado);
    }

    // ===== Actualizar por ID =====
    @Transactional
    public EstadoDTO actualizarPorId(Long id, @Valid EstadoDTO dto) {
        Estado e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Estado con id: " + id));

        if (notBlank(dto.getNombreEstado())
                && !dto.getNombreEstado().equalsIgnoreCase(e.getNombreEstado())) {
            if (repo.existsByNombreEstadoIgnoreCase(dto.getNombreEstado()))
                throw new IllegalArgumentException("El nuevo nombreEstado ya existe.");
            e.setNombreEstado(dto.getNombreEstado().trim());
        }

        Estado actualizado = repo.save(e);
        log.info("Estado actualizado: {} (id={})", actualizado.getNombreEstado(), id);
        return toDTO(actualizado);
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Estado eliminado id={}", id);
        return true;
    }
}
