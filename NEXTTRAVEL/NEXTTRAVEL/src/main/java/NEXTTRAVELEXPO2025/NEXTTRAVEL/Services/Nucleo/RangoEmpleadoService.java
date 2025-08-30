package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.RangoEmpleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.RangoEmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.RangoEmpleadoRepository;
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
public class RangoEmpleadoService {

    private final RangoEmpleadoRepository repo;

    // ===== Helpers =====
    private RangoEmpleadoDTO toDTO(RangoEmpleado e) {
        return RangoEmpleadoDTO.builder()
                .nombreRango(e.getNombreRango())
                .salarioBase(e.getSalarioBase())
                .build();
    }
    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    // ===== Listado / Búsquedas =====
    public Page<RangoEmpleadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<RangoEmpleadoDTO> buscarPorNombre(String q, Pageable pageable) {
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
        if (repo.existsByNombreRangoIgnoreCase(dto.getNombreRango()))
            throw new IllegalArgumentException("El nombre de rango ya existe.");

        RangoEmpleado e = RangoEmpleado.builder()
                .nombreRango(dto.getNombreRango().trim())
                .salarioBase(dto.getSalarioBase())
                .build();

        RangoEmpleado guardado = repo.save(e);
        log.info("RangoEmpleado creado: {}", guardado.getNombreRango());
        return toDTO(guardado);
    }

    // ===== Actualizar por ID =====
    @Transactional
    public RangoEmpleadoDTO actualizarPorId(Long id, @Valid RangoEmpleadoDTO dto) {
        RangoEmpleado e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró RangoEmpleado con id: " + id));

        if (notBlank(dto.getNombreRango())
                && !dto.getNombreRango().equalsIgnoreCase(e.getNombreRango())) {
            if (repo.existsByNombreRangoIgnoreCase(dto.getNombreRango()))
                throw new IllegalArgumentException("El nuevo nombre de rango ya existe.");
            e.setNombreRango(dto.getNombreRango().trim());
        }

        if (dto.getSalarioBase() != null) {
            e.setSalarioBase(dto.getSalarioBase());
        }

        RangoEmpleado actualizado = repo.save(e);
        log.info("RangoEmpleado actualizado: {} (id={})", actualizado.getNombreRango(), id);
        return toDTO(actualizado);
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("RangoEmpleado eliminado id={}", id);
        return true;
    }
}