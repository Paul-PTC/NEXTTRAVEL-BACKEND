package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Promocion;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PromocionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PromocionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            if (v < 10 || v > 99) throw new IllegalArgumentException("descuentoPorcentaje debe ser 10..99 o null");
        }
        if (d.getFechaInicio() != null && d.getFechaFin() != null) {
            if (d.getFechaFin().isBefore(d.getFechaInicio()))
                throw new IllegalArgumentException("fechaFin no puede ser anterior a fechaInicio");
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
    public Page<PromocionDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }
    public Page<PromocionDTO> buscarPorNombre(String q, Pageable p) { return repo.findByNombreContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<PromocionDTO> buscarPorDescripcion(String q, Pageable p) { return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<PromocionDTO> buscarPorDescuento(Integer n, Pageable p) { return repo.findByDescuentoPorcentaje(n, p).map(this::toDTO); }
    public Page<PromocionDTO> buscarPorDescuentoRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 10;
        int to   = (max != null) ? max : 99;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByDescuentoPorcentajeBetween(from, to, p).map(this::toDTO);
    }
    public Page<PromocionDTO> buscarVigentes(java.time.LocalDate d, Pageable p) {
        return repo.findVigentesEn(d, p).map(this::toDTO);
    }
    public Page<PromocionDTO> buscarPorRango(java.time.LocalDate desde, java.time.LocalDate hasta, Pageable p) {
        return repo.findByRangoSolapado(desde, hasta, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid PromocionDTO dto) {
        validate(dto);
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
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid PromocionDTO dto) {
        Promocion e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Promocion con id: " + id));

        // Previsualizar valores nuevos para validar (sin mutar aún)
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
        if (dto.getDescuentoPorcentaje() != null || dto.getDescuentoPorcentaje() == null) e.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        if (dto.getFechaInicio() != null) e.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) e.setFechaFin(dto.getFechaFin());

        repo.save(e);
        log.info("Promocion actualizada id={}", id);
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Promocion eliminada id={}", id);
        return true;
    }
}