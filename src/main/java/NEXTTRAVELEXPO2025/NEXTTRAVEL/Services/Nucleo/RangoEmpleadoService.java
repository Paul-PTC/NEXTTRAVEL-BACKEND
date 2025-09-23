package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.RangoEmpleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.RangoEmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.RangoEmpleadoRepository;
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
public class RangoEmpleadoService {

    private final RangoEmpleadoRepository repo;

    // ===== Helpers =====
    private RangoEmpleadoDTO toDTO(RangoEmpleado e) {
        return RangoEmpleadoDTO.builder()
                .nombreRango(e.getNombreRango())
                .salarioBase(e.getSalarioBase())
                .build();
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    // ===== Listado / Búsquedas =====
    public Page<RangoEmpleadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<RangoEmpleadoDTO> buscarPorNombre(String q, Pageable pageable) {
        if (!notBlank(q)) {
            throw new BadRequestException("El parámetro 'nombreRango' no puede estar vacío.");
        }
        return repo.findByNombreRangoContainingIgnoreCase(q, pageable).map(this::toDTO);
    }

    public Page<RangoEmpleadoDTO> buscarPorSalario(BigDecimal min, BigDecimal max, Pageable pageable) {
        BigDecimal from = (min != null) ? min : BigDecimal.ZERO;
        BigDecimal to   = (max != null) ? max : new BigDecimal("9999999999.99");

        if (from.compareTo(to) > 0) {
            // swap si vienen invertidos
            BigDecimal tmp = from; from = to; to = tmp;
        }

        return repo.findBySalarioBaseBetween(from, to, pageable).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public RangoEmpleadoDTO crear(@Valid RangoEmpleadoDTO dto) {
        if (!notBlank(dto.getNombreRango())) {
            throw new BadRequestException("El campo 'nombreRango' es obligatorio.");
        }

        if (dto.getSalarioBase() == null || dto.getSalarioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El campo 'salarioBase' debe ser mayor a 0.");
        }

        if (repo.existsByNombreRangoIgnoreCase(dto.getNombreRango())) {
            throw new ConflictException("El nombre de rango ya existe: " + dto.getNombreRango());
        }

        try {
            RangoEmpleado e = RangoEmpleado.builder()
                    .nombreRango(dto.getNombreRango().trim())
                    .salarioBase(dto.getSalarioBase())
                    .build();

            RangoEmpleado guardado = repo.save(e);
            log.info("RangoEmpleado creado: {}", guardado.getNombreRango());
            return toDTO(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear RangoEmpleado: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public RangoEmpleadoDTO actualizarPorId(Long id, @Valid RangoEmpleadoDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        RangoEmpleado e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró RangoEmpleado con id: " + id));

        if (notBlank(dto.getNombreRango())
                && !dto.getNombreRango().equalsIgnoreCase(e.getNombreRango())) {
            if (repo.existsByNombreRangoIgnoreCase(dto.getNombreRango())) {
                throw new ConflictException("El nuevo nombre de rango ya existe: " + dto.getNombreRango());
            }
            e.setNombreRango(dto.getNombreRango().trim());
        }

        if (dto.getSalarioBase() != null && dto.getSalarioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El campo 'salarioBase' debe ser mayor a 0.");
        }

        if (dto.getSalarioBase() != null) {
            e.setSalarioBase(dto.getSalarioBase());
        }

        try {
            RangoEmpleado actualizado = repo.save(e);
            log.info("RangoEmpleado actualizado: {} (id={})", actualizado.getNombreRango(), id);
            return toDTO(actualizado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar RangoEmpleado: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("No existe RangoEmpleado con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("RangoEmpleado eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el RangoEmpleado con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}