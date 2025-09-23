package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.DetalleMantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.Mantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.DetalleMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.DetalleMantenimientoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.MantenimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetalleMantenimientoService {

    private final DetalleMantenimientoRepository repo;
    private final MantenimientoRepository mantRepo;

    private DetalleMantenimientoDTO toDTO(DetalleMantenimiento e) {
        return DetalleMantenimientoDTO.builder()
                .idMantenimiento(e.getMantenimiento() != null ? e.getMantenimiento().getIdMantenimiento() : null)
                .actividad(e.getActividad())
                .costo(e.getCosto())
                .build();
    }

    private void validateCosto(BigDecimal costo) {
        if (costo != null && costo.signum() < 0)
            throw new IllegalArgumentException("El costo debe ser mayor o igual a 0.");
    }

    private void validateActividad(String actividad) {
        if (actividad == null || actividad.isBlank()) {
            throw new IllegalArgumentException("La actividad es obligatoria.");
        }
    }

    // ===== Listado / Búsquedas =====
    public Page<DetalleMantenimientoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<DetalleMantenimientoDTO> buscarPorMantenimiento(Long idMantenimiento, Pageable p) {
        return repo.findByMantenimiento_IdMantenimiento(idMantenimiento, p).map(this::toDTO);
    }

    public Page<DetalleMantenimientoDTO> buscarPorActividad(String q, Pageable p) {
        return repo.findByActividadContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<DetalleMantenimientoDTO> buscarPorCosto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : new BigDecimal("0.00");
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) {
            BigDecimal t = from; from = to; to = t;
        }
        return repo.findByCostoBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid DetalleMantenimientoDTO dto) {
        if (dto.getIdMantenimiento() == null) {
            throw new IllegalArgumentException("El idMantenimiento es obligatorio.");
        }
        validateActividad(dto.getActividad());
        validateCosto(dto.getCosto());

        Mantenimiento m = mantRepo.findById(dto.getIdMantenimiento())
                .orElseThrow(() -> new EntityNotFoundException("No existe Mantenimiento con id: " + dto.getIdMantenimiento()));

        try {
            DetalleMantenimiento e = DetalleMantenimiento.builder()
                    .mantenimiento(m)
                    .actividad(dto.getActividad().trim())
                    .costo(dto.getCosto())
                    .build();

            DetalleMantenimiento g = repo.save(e);
            log.info("DetalleMantenimiento creado id={} mant={} actividad={}",
                    g.getIdDetalleMantenimiento(), m.getIdMantenimiento(), g.getActividad());
            return g.getIdDetalleMantenimiento();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear el detalle de mantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid DetalleMantenimientoDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("El id del detalle de mantenimiento es obligatorio para actualizar.");
        }

        DetalleMantenimiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró DetalleMantenimiento con id: " + id));

        if (dto.getIdMantenimiento() != null &&
                (e.getMantenimiento() == null || !dto.getIdMantenimiento().equals(e.getMantenimiento().getIdMantenimiento()))) {
            Mantenimiento m = mantRepo.findById(dto.getIdMantenimiento())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Mantenimiento con id: " + dto.getIdMantenimiento()));
            e.setMantenimiento(m);
        }

        if (dto.getActividad() != null) {
            validateActividad(dto.getActividad());
            e.setActividad(dto.getActividad().trim());
        }

        if (dto.getCosto() != null) {
            validateCosto(dto.getCosto());
            e.setCosto(dto.getCosto());
        }

        try {
            repo.save(e);
            log.info("DetalleMantenimiento actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar el detalle de mantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del detalle de mantenimiento es obligatorio para eliminar.");
        }

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se encontró DetalleMantenimiento con id=" + id);
        }

        try {
            repo.deleteById(id);
            log.info("DetalleMantenimiento eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar el detalle de mantenimiento id=" + id
                    + " debido a restricciones en la base de datos.", ex);
        }
    }
}
