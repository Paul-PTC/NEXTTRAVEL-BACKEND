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
        if (n == null || n <= 0) {
            throw new IllegalArgumentException("El valor de puntos debe ser mayor a 0.");
        }
        return repo.findByPuntosRequeridos(n, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorPuntosRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from <= 0) throw new IllegalArgumentException("El mínimo de puntos debe ser mayor a 0.");
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosRequeridosBetween(from, to, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorDescuentoExacto(Integer n, Pageable p) {
        if (n == null || n < 10 || n > 99) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 10 y 99.");
        }
        return repo.findByPorcentajeDescuento(n, p).map(this::toDTO);
    }

    public Page<ReglaPuntosDescuentoDTO> buscarPorDescuentoRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 10;
        int to   = (max != null) ? max : 99;
        if (from < 10) throw new IllegalArgumentException("El mínimo de descuento debe ser al menos 10%.");
        if (to > 99) throw new IllegalArgumentException("El máximo de descuento no puede superar 99%.");
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPorcentajeDescuentoBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid ReglaPuntosDescuentoDTO dto) {
        if (dto.getPuntosRequeridos() == null || dto.getPuntosRequeridos() <= 0) {
            throw new IllegalArgumentException("Los puntos requeridos son obligatorios y deben ser > 0.");
        }
        if (dto.getPorcentajeDescuento() == null ||
                dto.getPorcentajeDescuento() < 10 || dto.getPorcentajeDescuento() > 99) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 10 y 99.");
        }

        if (repo.existsByPuntosRequeridos(dto.getPuntosRequeridos())) {
            throw new IllegalArgumentException("Ya existe una regla con puntosRequeridos=" + dto.getPuntosRequeridos());
        }
        if (repo.existsByPorcentajeDescuento(dto.getPorcentajeDescuento())) {
            throw new IllegalArgumentException("Ya existe una regla con porcentajeDescuento=" + dto.getPorcentajeDescuento());
        }

        try {
            ReglaPuntosDescuento e = ReglaPuntosDescuento.builder()
                    .puntosRequeridos(dto.getPuntosRequeridos())
                    .porcentajeDescuento(dto.getPorcentajeDescuento())
                    .build();
            ReglaPuntosDescuento g = repo.save(e);
            log.info("Regla creada id={} puntos={} descuento={}%"
                    , g.getIdRegla(), g.getPuntosRequeridos(), g.getPorcentajeDescuento());
            return g.getIdRegla();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear la regla: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid ReglaPuntosDescuentoDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la regla es obligatorio.");
        }

        ReglaPuntosDescuento e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Regla con id: " + id));

        if (dto.getPuntosRequeridos() != null) {
            if (dto.getPuntosRequeridos() <= 0) {
                throw new IllegalArgumentException("Los puntos requeridos deben ser > 0.");
            }
            if (!dto.getPuntosRequeridos().equals(e.getPuntosRequeridos()) &&
                    repo.existsByPuntosRequeridos(dto.getPuntosRequeridos())) {
                throw new IllegalArgumentException("Ya existe una regla con puntosRequeridos=" + dto.getPuntosRequeridos());
            }
            e.setPuntosRequeridos(dto.getPuntosRequeridos());
        }

        if (dto.getPorcentajeDescuento() != null) {
            if (dto.getPorcentajeDescuento() < 10 || dto.getPorcentajeDescuento() > 99) {
                throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 10 y 99.");
            }
            if (!dto.getPorcentajeDescuento().equals(e.getPorcentajeDescuento()) &&
                    repo.existsByPorcentajeDescuento(dto.getPorcentajeDescuento())) {
                throw new IllegalArgumentException("Ya existe una regla con porcentajeDescuento=" + dto.getPorcentajeDescuento());
            }
            e.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        }

        try {
            repo.save(e);
            log.info("Regla actualizada id={} puntos={} descuento={}"
                    , id, e.getPuntosRequeridos(), e.getPorcentajeDescuento());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar la regla: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la regla es obligatorio.");
        }

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("No se encontró ninguna regla con id=" + id);
        }

        try {
            repo.deleteById(id);
            log.info("Regla eliminada id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se pudo eliminar la regla id=" + id + " debido a restricciones en BD.", ex);
        }
    }
}
