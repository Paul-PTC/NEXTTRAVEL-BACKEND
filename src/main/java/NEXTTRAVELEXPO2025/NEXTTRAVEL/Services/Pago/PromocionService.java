package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Promocion;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PromocionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PromocionRepository;
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
public class PromocionService {

    private final PromocionRepository repo;

    private void validate(PromocionDTO d) {
        if (d.getDescuentoPorcentaje() != null) {
            int v = d.getDescuentoPorcentaje();
            if (v < 10 || v > 99) {
                throw new BadRequestException("El campo 'descuentoPorcentaje' debe estar entre 10 y 99 o ser null.");
            }
        }
        if (d.getFechaInicio() != null && d.getFechaFin() != null) {
            if (d.getFechaFin().isBefore(d.getFechaInicio())) {
                throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }
        }
    }

    private PromocionDTO toDTO(Promocion e) {
        return PromocionDTO.builder()
                .idPromocion(e.getIdPromocion())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .descuentoPorcentaje(e.getDescuentoPorcentaje())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<PromocionDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarPorNombre(String q, Pageable p) {
        if (q == null || q.isBlank()) throw new BadRequestException("El nombre de búsqueda no puede estar vacío.");
        return repo.findByNombreContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarPorDescripcion(String q, Pageable p) {
        if (q == null || q.isBlank()) throw new BadRequestException("La descripción de búsqueda no puede estar vacía.");
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarPorDescuento(Integer n, Pageable p) {
        if (n == null || n < 10 || n > 99) throw new BadRequestException("El descuento debe estar entre 10 y 99.");
        return repo.findByDescuentoPorcentaje(n, p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarPorDescuentoRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 10;
        int to   = (max != null) ? max : 99;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByDescuentoPorcentajeBetween(from, to, p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarVigentes(java.time.LocalDate d, Pageable p) {
        if (d == null) throw new BadRequestException("La fecha es obligatoria para buscar promociones vigentes.");
        return repo.findVigentesEn(d, p).map(this::toDTO);
    }

    public Page<PromocionDTO> buscarPorRango(java.time.LocalDate desde, java.time.LocalDate hasta, Pageable p) {
        if (desde == null || hasta == null) throw new BadRequestException("Ambas fechas son obligatorias para buscar por rango.");
        return repo.findByRangoSolapado(desde, hasta, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid PromocionDTO dto) {
        validate(dto);
        try {
            Promocion e = Promocion.builder()
                    .nombre(dto.getNombre())
                    .descripcion(dto.getDescripcion())
                    .descuentoPorcentaje(dto.getDescuentoPorcentaje())
                    .fechaInicio(dto.getFechaInicio())
                    .fechaFin(dto.getFechaFin())
                    .build();

            Promocion g = repo.save(e);
            log.info("Promocion creada id={} nombre={}", g.getIdPromocion(), g.getNombre());
            return g.getIdPromocion();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Promoción: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid PromocionDTO dto) {
        if (id == null || id <= 0) throw new BadRequestException("El id proporcionado no es válido.");

        Promocion e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Promocion con id: " + id));

        PromocionDTO preview = PromocionDTO.builder()
                .nombre(dto.getNombre() != null ? dto.getNombre() : e.getNombre())
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion() : e.getDescripcion())
                .descuentoPorcentaje(dto.getDescuentoPorcentaje() != null ? dto.getDescuentoPorcentaje() : e.getDescuentoPorcentaje())
                .fechaInicio(dto.getFechaInicio() != null ? dto.getFechaInicio() : e.getFechaInicio())
                .fechaFin(dto.getFechaFin() != null ? dto.getFechaFin() : e.getFechaFin())
                .build();
        validate(preview);

        if (dto.getNombre() != null) e.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        e.setDescuentoPorcentaje(dto.getDescuentoPorcentaje()); // acepta null
        if (dto.getFechaInicio() != null) e.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) e.setFechaFin(dto.getFechaFin());

        try {
            repo.save(e);
            log.info("Promocion actualizada id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Promoción: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null || id <= 0) throw new BadRequestException("El id proporcionado no es válido.");
        if (!repo.existsById(id)) throw new ResourceNotFoundException("No existe Promoción con id: " + id);

        try {
            repo.deleteById(id);
            log.info("Promocion eliminada id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar la Promoción con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}