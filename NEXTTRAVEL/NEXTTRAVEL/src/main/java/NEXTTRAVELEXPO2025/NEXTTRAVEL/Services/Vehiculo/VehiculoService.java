package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.Vehiculo;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.TipoMantenimientoMinDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.VehiculoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.VehiculoPlacaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.TipoMantenimientoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository repo;
    private final TipoMantenimientoRepository Trepo;


    private VehiculoDTO toDTO(Vehiculo e) {
        return VehiculoDTO.builder()
                .idVehiculo(e.getIdVehiculo())
                .placa(e.getPlaca())
                .modelo(e.getModelo())
                .capacidad(e.getCapacidad())
                .anioFabricacion(e.getAnioFabricacion())
                .estado(e.getEstado())
                .build();
    }

    // Listar solo id + placa
    public List<VehiculoPlacaDTO> listarSoloPlacas() {
        return repo.findAll().stream()
                .map(v -> new VehiculoPlacaDTO(
                        v.getIdVehiculo(),
                        v.getPlaca()
                ))
                .collect(Collectors.toList());
    }



    // ===== Listado / Búsquedas =====
    public Page<VehiculoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<VehiculoDTO> buscarPorPlaca(String q, Pageable p) {
        return repo.findByPlacaContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VehiculoDTO> buscarPorModelo(String q, Pageable p) {
        return repo.findByModeloContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VehiculoDTO> buscarPorEstado(String q, Pageable p) {
        return repo.findByEstadoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VehiculoDTO> buscarPorCapacidad(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByCapacidadBetween(from, to, p).map(this::toDTO);
    }

    public Page<VehiculoDTO> buscarPorAnio(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1900;
        int to   = (max != null) ? max : 2100;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByAnioFabricacionBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid VehiculoDTO dto) {
        String placa = dto.getPlaca().trim();
        if (repo.existsByPlacaIgnoreCase(placa))
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");

        try {
            Vehiculo e = Vehiculo.builder()
                    .placa(placa)
                    .modelo(dto.getModelo().trim())
                    .capacidad(dto.getCapacidad())
                    .anioFabricacion(dto.getAnioFabricacion())
                    .estado(dto.getEstado()) // si es null, DB usará 'Activo'
                    .build();
            Vehiculo g = repo.save(e);
            log.info("Vehiculo creado id={} placa={}", g.getIdVehiculo(), g.getPlaca());
            return g.getIdVehiculo();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad/validación en la base de datos.", ex);
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid VehiculoDTO dto) {
        Vehiculo e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Vehiculo con id: " + id));

        if (dto.getPlaca() != null) {
            String nueva = dto.getPlaca().trim();
            if (!nueva.equalsIgnoreCase(e.getPlaca()) && repo.existsByPlacaIgnoreCase(nueva)) {
                throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
            }
            e.setPlaca(nueva);
        }
        if (dto.getModelo() != null) e.setModelo(dto.getModelo().trim());
        if (dto.getCapacidad() != null) {
            if (dto.getCapacidad() < 1) throw new IllegalArgumentException("capacidad debe ser >= 1");
            e.setCapacidad(dto.getCapacidad());
        }
        if (dto.getAnioFabricacion() != null) {
            int y = dto.getAnioFabricacion();
            if (y < 1900 || y > 2100) throw new IllegalArgumentException("anioFabricacion fuera de rango (1900..2100)");
            e.setAnioFabricacion(y);
        }
        if (dto.getEstado() != null) e.setEstado(dto.getEstado());

        try {
            repo.save(e);
            log.info("Vehiculo actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad/validación en la base de datos.", ex);
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Vehiculo eliminado id={}", id);
        return true;
    }
}