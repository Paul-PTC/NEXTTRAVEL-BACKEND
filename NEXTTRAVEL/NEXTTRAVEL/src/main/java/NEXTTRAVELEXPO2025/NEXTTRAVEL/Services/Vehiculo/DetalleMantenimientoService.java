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
            throw new IllegalArgumentException("costo debe ser >= 0");
    }

    // ===== Listado / Búsquedas =====
    public Page<DetalleMantenimientoDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<DetalleMantenimientoDTO> buscarPorMantenimiento(Long idMantenimiento, Pageable p) {
        return repo.findByMantenimiento_IdMantenimiento(idMantenimiento, p).map(this::toDTO);
    }

    public Page<DetalleMantenimientoDTO> buscarPorActividad(String q, Pageable p) {
        return repo.findByActividadContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<DetalleMantenimientoDTO> buscarPorCosto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : new BigDecimal("0.00");
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) { BigDecimal t = from; from = to; to = t; }
        return repo.findByCostoBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid DetalleMantenimientoDTO dto) {
        Mantenimiento m = mantRepo.findById(dto.getIdMantenimiento())
                .orElseThrow(() -> new EntityNotFoundException("No existe Mantenimiento con id: " + dto.getIdMantenimiento()));

        validateCosto(dto.getCosto());

        DetalleMantenimiento e = DetalleMantenimiento.builder()
                .mantenimiento(m)
                .actividad(dto.getActividad())
                .costo(dto.getCosto())
                .build();

        DetalleMantenimiento g = repo.save(e);
        log.info("DetalleMantenimiento creado id={} mant={} actividad={}",
                g.getIdDetalleMantenimiento(), m.getIdMantenimiento(), g.getActividad());
        return g.getIdDetalleMantenimiento();
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid DetalleMantenimientoDTO dto) {
        DetalleMantenimiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró DetalleMantenimiento con id: " + id));

        if (dto.getIdMantenimiento() != null &&
                (e.getMantenimiento() == null || !dto.getIdMantenimiento().equals(e.getMantenimiento().getIdMantenimiento()))) {
            Mantenimiento m = mantRepo.findById(dto.getIdMantenimiento())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Mantenimiento con id: " + dto.getIdMantenimiento()));
            e.setMantenimiento(m);
        }

        if (dto.getActividad() != null) e.setActividad(dto.getActividad());
        if (dto.getCosto() != null) {
            validateCosto(dto.getCosto());
            e.setCosto(dto.getCosto());
        }

        repo.save(e);
        log.info("DetalleMantenimiento actualizado id={}", id);
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("DetalleMantenimiento eliminado id={}", id);
        return true;
    }
}
