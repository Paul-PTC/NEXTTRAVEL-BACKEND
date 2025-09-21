package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.Mantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.TipoMantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.Vehiculo;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.ListarTipoMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.MantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.TipoMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.MantenimientoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.TipoMantenimientoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MantenimientoService {

    private final MantenimientoRepository repo;
    private final VehiculoRepository vehiculoRepo;
    private final TipoMantenimientoRepository tipoRepo;

    // ===== Listar solo tipos de mantenimiento =====
    public List<ListarTipoMantenimientoDTO> listarTipos() {
        return tipoRepo.findAll()
                .stream()
                .map(tm -> new ListarTipoMantenimientoDTO(
                        tm.getIdTipoMantenimiento(),
                        tm.getNombreTipo()
                ))
                .toList();
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid MantenimientoDTO dto) {
        if (dto.getIdVehiculo() == null) {
            throw new IllegalArgumentException("El idVehiculo es obligatorio.");
        }
        if (dto.getIdTipoMantenimiento() == null) {
            throw new IllegalArgumentException("El idTipoMantenimiento es obligatorio.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        Vehiculo v = vehiculoRepo.findById(dto.getIdVehiculo())
                .orElseThrow(() -> new EntityNotFoundException("No existe Vehiculo con id: " + dto.getIdVehiculo()));
        TipoMantenimiento tm = tipoRepo.findById(dto.getIdTipoMantenimiento())
                .orElseThrow(() -> new EntityNotFoundException("No existe TipoMantenimiento con id: " + dto.getIdTipoMantenimiento()));

        try {
            Mantenimiento e = Mantenimiento.builder()
                    .vehiculo(v)
                    .tipoMantenimiento(tm)
                    .descripcion(dto.getDescripcion().trim())
                    .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                    .build();

            Mantenimiento g = repo.save(e);
            log.info("Mantenimiento creado id={} vehiculo={} tipo={}",
                    g.getIdMantenimiento(), v.getIdVehiculo(), tm.getIdTipoMantenimiento());
            return g.getIdMantenimiento();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear el mantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid MantenimientoDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("El id del mantenimiento es obligatorio para actualizar.");
        }

        Mantenimiento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Mantenimiento con id: " + id));

        if (dto.getIdVehiculo() != null &&
                (e.getVehiculo() == null || !dto.getIdVehiculo().equals(e.getVehiculo().getIdVehiculo()))) {
            Vehiculo v = vehiculoRepo.findById(dto.getIdVehiculo())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Vehiculo con id: " + dto.getIdVehiculo()));
            e.setVehiculo(v);
        }

        if (dto.getIdTipoMantenimiento() != null &&
                (e.getTipoMantenimiento() == null || !dto.getIdTipoMantenimiento().equals(e.getTipoMantenimiento().getIdTipoMantenimiento()))) {
            TipoMantenimiento tm = tipoRepo.findById(dto.getIdTipoMantenimiento())
                    .orElseThrow(() -> new EntityNotFoundException("No existe TipoMantenimiento con id: " + dto.getIdTipoMantenimiento()));
            e.setTipoMantenimiento(tm);
        }

        if (dto.getDescripcion() != null) {
            if (dto.getDescripcion().isBlank()) {
                throw new IllegalArgumentException("La descripción no puede estar vacía.");
            }
            e.setDescripcion(dto.getDescripcion().trim());
        }
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        try {
            repo.save(e);
            log.info("Mantenimiento actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar el mantenimiento: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del mantenimiento es obligatorio para eliminar.");
        }

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se encontró Mantenimiento con id=" + id);
        }

        try {
            repo.deleteById(id);
            log.info("Mantenimiento eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar el mantenimiento id=" + id
                    + " debido a restricciones en la base de datos.", ex);
        }
    }
}
