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

    // ===== Listar solo id + placa =====
    public List<VehiculoPlacaDTO> listarSoloPlacas() {
        return repo.findAll().stream()
                .map(v -> new VehiculoPlacaDTO(
                        v.getIdVehiculo(),
                        v.getPlaca()
                ))
                .toList();
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
        if (dto.getPlaca() == null || dto.getPlaca().isBlank())
            throw new IllegalArgumentException("La placa es obligatoria.");
        if (dto.getModelo() == null || dto.getModelo().isBlank())
            throw new IllegalArgumentException("El modelo es obligatorio.");
        if (dto.getCapacidad() == null || dto.getCapacidad() < 1)
            throw new IllegalArgumentException("La capacidad debe ser >= 1.");
        if (dto.getAnioFabricacion() == null ||
                dto.getAnioFabricacion() < 1900 || dto.getAnioFabricacion() > 2100)
            throw new IllegalArgumentException("El año de fabricación debe estar entre 1900 y 2100.");

        String placa = dto.getPlaca().trim();
        if (repo.existsByPlacaIgnoreCase(placa))
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");

        try {
            Vehiculo e = Vehiculo.builder()
                    .placa(placa)
                    .modelo(dto.getModelo().trim())
                    .capacidad(dto.getCapacidad())
                    .anioFabricacion(dto.getAnioFabricacion())
                    .estado(dto.getEstado() != null ? dto.getEstado() : "Activo")
                    .build();

            Vehiculo g = repo.save(e);
            log.info("Vehiculo creado id={} placa={}", g.getIdVehiculo(), g.getPlaca());
            return g.getIdVehiculo();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear vehículo: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid VehiculoDTO dto) {
        if (id == null) throw new IllegalArgumentException("El id es obligatorio para actualizar.");

        Vehiculo e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Vehiculo con id: " + id));

        if (dto.getPlaca() != null) {
            String nueva = dto.getPlaca().trim();
            if (nueva.isBlank())
                throw new IllegalArgumentException("La placa no puede estar vacía.");
            if (!nueva.equalsIgnoreCase(e.getPlaca()) && repo.existsByPlacaIgnoreCase(nueva)) {
                throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
            }
            e.setPlaca(nueva);
        }

        if (dto.getModelo() != null) {
            String nuevoModelo = dto.getModelo().trim();
            if (nuevoModelo.isBlank())
                throw new IllegalArgumentException("El modelo no puede estar vacío.");
            e.setModelo(nuevoModelo);
        }

        if (dto.getCapacidad() != null) {
            if (dto.getCapacidad() < 1)
                throw new IllegalArgumentException("capacidad debe ser >= 1.");
            e.setCapacidad(dto.getCapacidad());
        }

        if (dto.getAnioFabricacion() != null) {
            int y = dto.getAnioFabricacion();
            if (y < 1900 || y > 2100)
                throw new IllegalArgumentException("anioFabricacion fuera de rango (1900..2100).");
            e.setAnioFabricacion(y);
        }

        if (dto.getEstado() != null) {
            e.setEstado(dto.getEstado());
        }

        try {
            repo.save(e);
            log.info("Vehiculo actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar vehículo: "
                    + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) throw new IllegalArgumentException("El id es obligatorio para eliminar.");
        if (!repo.existsById(id)) throw new EntityNotFoundException("No se encontró vehículo con id=" + id);

        try {
            repo.deleteById(id);
            log.info("Vehiculo eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar vehículo id=" + id +
                    " debido a restricciones en la base de datos.", ex);
        }
    }
}