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
        return repo.findByNombreTipoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<TipoGastoDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid TipoGastoDTO dto) {
        if (repo.existsByNombreTipoIgnoreCase(dto.getNombreTipo()))
            throw new IllegalArgumentException("Ya existe un TipoGasto con ese nombre.");

        try {
            TipoGasto e = TipoGasto.builder()
                    .nombreTipo(dto.getNombreTipo().trim())
                    .descripcion(dto.getDescripcion())
                    .build();
            TipoGasto g = repo.save(e);
            log.info("TipoGasto creado id={} nombre={}", g.getIdTipoGasto(), g.getNombreTipo());
            return g.getIdTipoGasto();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad/validación en la base de datos.", ex);
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid TipoGastoDTO dto) {
        TipoGasto e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró TipoGasto con id: " + id));

        if (dto.getNombreTipo() != null) {
            String nuevo = dto.getNombreTipo().trim();
            // Si cambia y existe otro con ese nombre (case-insensitive)
            if (!nuevo.equalsIgnoreCase(e.getNombreTipo()) &&
                    repo.existsByNombreTipoIgnoreCase(nuevo)) {
                throw new IllegalArgumentException("Ya existe un TipoGasto con ese nombre.");
            }
            e.setNombreTipo(nuevo);
        }
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());

        try {
            repo.save(e);
            log.info("TipoGasto actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad/validación en la base de datos.", ex);
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("TipoGasto eliminado id={}", id);
        return true;
    }
}
