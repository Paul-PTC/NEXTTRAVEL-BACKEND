package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.TipoGasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.TipoGastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.TipoGastoRepository;
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
public class TipoGastoService {

    private final TipoGastoRepository repo;

    private TipoGastoDTO toDTO(TipoGasto e) {
        return TipoGastoDTO.builder()
                .idTipoGasto(e.getIdTipoGasto())
                .nombreTipo(e.getNombreTipo())
                .descripcion(e.getDescripcion())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<TipoGastoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<TipoGastoDTO> buscarPorNombre(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new IllegalArgumentException("El parámetro de búsqueda por nombre no puede estar vacío.");
        }
        return repo.findByNombreTipoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<TipoGastoDTO> buscarPorDescripcion(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new IllegalArgumentException("El parámetro de búsqueda por descripción no puede estar vacío.");
        }
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid TipoGastoDTO dto) {
        if (dto.getNombreTipo() == null || dto.getNombreTipo().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de gasto es obligatorio.");
        }

        if (repo.existsByNombreTipoIgnoreCase(dto.getNombreTipo())) {
            throw new IllegalArgumentException("Ya existe un TipoGasto con ese nombre.");
        }

        try {
            TipoGasto e = TipoGasto.builder()
                    .nombreTipo(dto.getNombreTipo().trim())
                    .descripcion(dto.getDescripcion())
                    .build();

            TipoGasto g = repo.save(e);
            log.info("TipoGasto creado id={} nombre={}", g.getIdTipoGasto(), g.getNombreTipo());
            return g.getIdTipoGasto();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad o validación en la base de datos: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid TipoGastoDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("El id del TipoGasto es obligatorio para actualizar.");
        }

        TipoGasto e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró TipoGasto con id: " + id));

        if (dto.getNombreTipo() != null) {
            String nuevo = dto.getNombreTipo().trim();
            if (nuevo.isBlank()) {
                throw new IllegalArgumentException("El nombre del tipo de gasto no puede estar vacío.");
            }
            if (!nuevo.equalsIgnoreCase(e.getNombreTipo()) &&
                    repo.existsByNombreTipoIgnoreCase(nuevo)) {
                throw new IllegalArgumentException("Ya existe un TipoGasto con ese nombre.");
            }
            e.setNombreTipo(nuevo);
        }

        if (dto.getDescripcion() != null) {
            if (dto.getDescripcion().isBlank()) {
                throw new IllegalArgumentException("La descripción no puede ser vacía si se especifica.");
            }
            e.setDescripcion(dto.getDescripcion());
        }

        try {
            repo.save(e);
            log.info("TipoGasto actualizado id={} nombre={}", id, e.getNombreTipo());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad o validación en la base de datos: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del TipoGasto es obligatorio para eliminar.");
        }

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se encontró TipoGasto con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("TipoGasto eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar el TipoGasto id=" + id
                    + " porque está en uso en la base de datos.", ex);
        }
    }
}
