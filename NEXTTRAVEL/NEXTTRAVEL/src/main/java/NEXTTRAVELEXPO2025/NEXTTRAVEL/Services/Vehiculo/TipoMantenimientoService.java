package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.TipoMantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.TipoMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.TipoMantenimientoMinDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.TipoMantenimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoMantenimientoService {

    private final TipoMantenimientoRepository repo;

    private TipoMantenimientoDTO toDTO(TipoMantenimiento e) {
        return TipoMantenimientoDTO.builder()
                .idTipoMantenimiento(e.getIdTipoMantenimiento())
                .nombreTipo(e.getNombreTipo())
                .descripcion(e.getDescripcion())
                .build();
    }

    // ===== Listar solo ID y nombre =====
    public List<TipoMantenimientoMinDTO> listarSoloTipos() {
        return repo.findAll().stream()
                .map(t -> new TipoMantenimientoMinDTO(
                        t.getIdTipoMantenimiento(),
                        t.getNombreTipo()
                ))
                .toList();
    }

    // ===== Listado / Búsquedas =====
    public Page<TipoMantenimientoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<TipoMantenimientoDTO> buscarPorNombre(String q, Pageable p) {
        return repo.findByNombreTipoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<TipoMantenimientoDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid TipoMantenimientoDTO dto) {
        if (dto.getNombreTipo() == null || dto.getNombreTipo().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de mantenimiento es obligatorio.");
        }

        String nombre = dto.getNombreTipo().trim();

        if (repo.existsByNombreTipoIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe un TipoMantenimiento con ese nombre.");
        }

        try {
            TipoMantenimiento e = TipoMantenimiento.builder()
                    .nombreTipo(nombre)
                    .descripcion(dto.getDescripcion())
                    .build();

            TipoMantenimiento g = repo.save(e);
            log.info("TipoMantenimiento creado id={} nombre={}", g.getIdTipoMantenimiento(), g.getNombreTipo());
            return g.getIdTipoMantenimiento();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear TipoMantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid TipoMantenimientoDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("El id es obligatorio para actualizar.");
        }

        TipoMantenimiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró TipoMantenimiento con id: " + id));

        if (dto.getNombreTipo() != null) {
            String nuevo = dto.getNombreTipo().trim();
            if (nuevo.isBlank()) {
                throw new IllegalArgumentException("El nombre del tipo de mantenimiento no puede estar vacío.");
            }
            if (!nuevo.equalsIgnoreCase(e.getNombreTipo()) &&
                    repo.existsByNombreTipoIgnoreCase(nuevo)) {
                throw new IllegalArgumentException("Ya existe un TipoMantenimiento con ese nombre.");
            }
            e.setNombreTipo(nuevo);
        }

        if (dto.getDescripcion() != null) {
            e.setDescripcion(dto.getDescripcion());
        }

        try {
            repo.save(e);
            log.info("TipoMantenimiento actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar TipoMantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id es obligatorio para eliminar.");
        }

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se encontró TipoMantenimiento con id=" + id);
        }

        try {
            repo.deleteById(id);
            log.info("TipoMantenimiento eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar el TipoMantenimiento id=" + id
                    + " debido a restricciones en la base de datos.", ex);
        }
    }
}
