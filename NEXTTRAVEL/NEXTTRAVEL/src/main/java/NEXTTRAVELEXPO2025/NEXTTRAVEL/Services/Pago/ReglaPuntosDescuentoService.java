package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.ReglaPuntosDescuento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.ReglaPuntosDescuentoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.ReglaPuntosDescuentoRepository;
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
public class ReglaPuntosDescuentoService {

    private final ReglaPuntosDescuentoRepository repo;

    private ReglaPuntosDescuentoDTO toDTO(ReglaPuntosDescuento e) {
        return ReglaPuntosDescuentoDTO.builder()
                .idRegla(e.getIdRegla())
                .puntosRequeridos(e.getPuntosRequeridos())
                .porcentajeDescuento(e.getPorcentajeDescuento())
                .build();
    }

    // ===== Listar / Buscar =====
    public Page<ReglaPuntosDescuentoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorPuntosExacto(Integer n, Pageable p) {
        return repo.findByPuntosRequeridos(n, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorPuntosRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosRequeridosBetween(from, to, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorDescuentoExacto(Integer n, Pageable p) {
        return repo.findByPorcentajeDescuento(n, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorDescuentoRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 10;
        int to   = (max != null) ? max : 99;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPorcentajeDescuentoBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid ReglaPuntosDescuentoDTO dto) {
        // pre-checks de unicidad
        if (repo.existsByPuntosRequeridos(dto.getPuntosRequeridos()))
            throw new IllegalArgumentException("Ya existe una regla con puntosRequeridos=" + dto.getPuntosRequeridos());
        if (repo.existsByPorcentajeDescuento(dto.getPorcentajeDescuento()))
            throw new IllegalArgumentException("Ya existe una regla con porcentajeDescuento=" + dto.getPorcentajeDescuento());

        try {
            ReglaPuntosDescuento e = ReglaPuntosDescuento.builder()
                    .puntosRequeridos(dto.getPuntosRequeridos())
                    .porcentajeDescuento(dto.getPorcentajeDescuento())
                    .build();
            ReglaPuntosDescuento g = repo.save(e);
            log.info("Regla creada id={} puntos={} descuento={}%",
                    g.getIdRegla(), g.getPuntosRequeridos(), g.getPorcentajeDescuento());
            return g.getIdRegla();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad o checks en la base de datos.", ex);
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid ReglaPuntosDescuentoDTO dto) {
        ReglaPuntosDescuento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Regla con id: " + id));

        // Si cambian los valores, validar unicidad
        if (dto.getPuntosRequeridos() != null && !dto.getPuntosRequeridos().equals(e.getPuntosRequeridos())) {
            if (repo.existsByPuntosRequeridos(dto.getPuntosRequeridos()))
                throw new IllegalArgumentException("Ya existe una regla con puntosRequeridos=" + dto.getPuntosRequeridos());
            e.setPuntosRequeridos(dto.getPuntosRequeridos());
        }
        if (dto.getPorcentajeDescuento() != null && !dto.getPorcentajeDescuento().equals(e.getPorcentajeDescuento())) {
            if (repo.existsByPorcentajeDescuento(dto.getPorcentajeDescuento()))
                throw new IllegalArgumentException("Ya existe una regla con porcentajeDescuento=" + dto.getPorcentajeDescuento());
            e.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        }

        try {
            repo.save(e);
            log.info("Regla actualizada id={} puntos={} descuento={}",
                    id, e.getPuntosRequeridos(), e.getPorcentajeDescuento());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de unicidad o checks en la base de datos.", ex);
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Regla eliminada id={}", id);
        return true;
    }
}
