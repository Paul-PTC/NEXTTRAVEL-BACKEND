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


    // Listar solo tipos de mantenimiento
    public List<ListarTipoMantenimientoDTO> listarTipos() {
        return tipoRepo.findAll()
                .stream()
                .map(tm -> new ListarTipoMantenimientoDTO(
                        tm.getIdTipoMantenimiento(),
                        tm.getNombreTipo()
                ))
                .toList();
    }

    @Transactional
    public Long crear(@Valid MantenimientoDTO dto) {
        Vehiculo v = vehiculoRepo.findById(dto.getIdVehiculo())
                .orElseThrow(() -> new EntityNotFoundException("No existe Vehiculo con id: " + dto.getIdVehiculo()));
        TipoMantenimiento tm = tipoRepo.findById(dto.getIdTipoMantenimiento())
                .orElseThrow(() -> new EntityNotFoundException("No existe TipoMantenimiento con id: " + dto.getIdTipoMantenimiento()));

        Mantenimiento e = Mantenimiento.builder()
                .vehiculo(v)
                .tipoMantenimiento(tm)
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        Mantenimiento g = repo.save(e);
        log.info("Mantenimiento creado id={} vehiculo={} tipo={}", g.getIdMantenimiento(), v.getIdVehiculo(), tm.getIdTipoMantenimiento());
        return g.getIdMantenimiento();
    }

    @Transactional
    public void actualizar(Long id, @Valid MantenimientoDTO dto) {
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

        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        repo.save(e);
        log.info("Mantenimiento actualizado id={}", id);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Mantenimiento eliminado id={}", id);
        return true;
    }
}
