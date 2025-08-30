package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.DescuentoAplicado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Promocion;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.ReglaPuntosDescuento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.DescuentoAplicadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.DescuentoAplicadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PromocionRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.ReglaPuntosDescuentoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DescuentoAplicadoService {

    private final DescuentoAplicadoRepository repo;
    private final ReservaRepository reservaRepo;
    private final PromocionRepository promoRepo;
    private final ReglaPuntosDescuentoRepository reglaRepo;

    private void validarYCompletarCampos(DescuentoAplicado e, @Valid DescuentoAplicadoDTO d) {
        String tipo = d.getTipoDescuento().toUpperCase();

        if ("PROMOCION".equals(tipo)) {
            if (d.getIdPromocion() == null)
                throw new IllegalArgumentException("idPromocion es obligatorio para tipo PROMOCION");
            if (d.getIdRegla() != null || d.getPuntosUsados() != null)
                throw new IllegalArgumentException("idRegla/puntosUsados deben ser nulos en tipo PROMOCION");

            Promocion p = promoRepo.findById(d.getIdPromocion())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Promocion con id: " + d.getIdPromocion()));
            e.setPromocion(p);
            e.setRegla(null);
            e.setPuntosUsados(null);

            Integer pct = p.getDescuentoPorcentaje();
            if (pct == null) throw new IllegalArgumentException("La promoción no tiene porcentaje configurado.");
            if (d.getPorcentajeAplicado() != null && !d.getPorcentajeAplicado().equals(pct))
                throw new IllegalArgumentException("porcentajeAplicado debe coincidir con la promoción (" + pct + "%).");
            e.setPorcentajeAplicado(pct);

        } else if ("PUNTOS".equals(tipo)) {
            if (d.getIdRegla() == null)
                throw new IllegalArgumentException("idRegla es obligatorio para tipo PUNTOS");
            if (d.getPuntosUsados() == null || d.getPuntosUsados() < 1)
                throw new IllegalArgumentException("puntosUsados debe ser > 0 para tipo PUNTOS");
            if (d.getIdPromocion() != null)
                throw new IllegalArgumentException("idPromocion debe ser nulo en tipo PUNTOS");

            ReglaPuntosDescuento r = reglaRepo.findById(d.getIdRegla())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Regla con id: " + d.getIdRegla()));
            e.setRegla(r);
            e.setPromocion(null);
            e.setPuntosUsados(d.getPuntosUsados());

            Integer pct = r.getPorcentajeDescuento();
            if (d.getPorcentajeAplicado() != null && !d.getPorcentajeAplicado().equals(pct))
                throw new IllegalArgumentException("porcentajeAplicado debe coincidir con la regla (" + pct + "%).");
            e.setPorcentajeAplicado(pct);

        } else { // MANUAL
            if (d.getPorcentajeAplicado() == null || d.getPorcentajeAplicado() < 10 || d.getPorcentajeAplicado() > 99)
                throw new IllegalArgumentException("porcentajeAplicado (10..99) es obligatorio en tipo MANUAL");
            if (d.getIdPromocion() != null || d.getIdRegla() != null || d.getPuntosUsados() != null)
                throw new IllegalArgumentException("idPromocion/idRegla/puntosUsados deben ser nulos en tipo MANUAL");

            e.setPromocion(null);
            e.setRegla(null);
            e.setPuntosUsados(null);
            e.setPorcentajeAplicado(d.getPorcentajeAplicado());
        }

        e.setTipoDescuento(tipo);
    }

    @Transactional
    public Long crear(@Valid DescuentoAplicadoDTO dto) {
        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));

        DescuentoAplicado e = new DescuentoAplicado();
        e.setReserva(r);
        e.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());

        validarYCompletarCampos(e, dto);

        DescuentoAplicado g = repo.save(e);
        log.info("DescuentoAplicado creado id={} reserva={} tipo={} pct={}",
                g.getIdDescuentoAplicado(), r.getIdReserva(), g.getTipoDescuento(), g.getPorcentajeAplicado());
        return g.getIdDescuentoAplicado();
    }

    @Transactional
    public void actualizar(Long id, @Valid DescuentoAplicadoDTO dto) {
        DescuentoAplicado e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró DescuentoAplicado con id: " + id));

        if (dto.getIdReserva() != null && (e.getReserva() == null ||
                !dto.getIdReserva().equals(e.getReserva().getIdReserva()))) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        if (dto.getFecha() != null) e.setFecha(dto.getFecha());
        if (dto.getTipoDescuento() != null) {
            // revalidar todo el bloque de negocio con el payload actual
            DescuentoAplicadoDTO merged = DescuentoAplicadoDTO.builder()
                    .idReserva(e.getReserva().getIdReserva())
                    .tipoDescuento(dto.getTipoDescuento())
                    .idPromocion(dto.getIdPromocion())
                    .idRegla(dto.getIdRegla())
                    .puntosUsados(dto.getPuntosUsados())
                    .porcentajeAplicado(dto.getPorcentajeAplicado())
                    .fecha(e.getFecha())
                    .build();
            validarYCompletarCampos(e, merged);
        } else {
            // Si no cambió tipo, permitir ajustes coherentes
            if (e.getTipoDescuento().equals("PROMOCION")) {
                if (dto.getIdPromocion() != null) {
                    Promocion p = promoRepo.findById(dto.getIdPromocion())
                            .orElseThrow(() -> new EntityNotFoundException("No existe Promocion con id: " + dto.getIdPromocion()));
                    e.setPromocion(p);
                    e.setPorcentajeAplicado(p.getDescuentoPorcentaje());
                }
            } else if (e.getTipoDescuento().equals("PUNTOS")) {
                if (dto.getIdRegla() != null) {
                    ReglaPuntosDescuento r = reglaRepo.findById(dto.getIdRegla())
                            .orElseThrow(() -> new EntityNotFoundException("No existe Regla con id: " + dto.getIdRegla()));
                    e.setRegla(r);
                    e.setPorcentajeAplicado(r.getPorcentajeDescuento());
                }
                if (dto.getPuntosUsados() != null) {
                    if (dto.getPuntosUsados() < 1) throw new IllegalArgumentException("puntosUsados debe ser > 0");
                    e.setPuntosUsados(dto.getPuntosUsados());
                }
            } else { // MANUAL
                if (dto.getPorcentajeAplicado() != null) {
                    int v = dto.getPorcentajeAplicado();
                    if (v < 10 || v > 99) throw new IllegalArgumentException("porcentajeAplicado debe ser 10..99");
                    e.setPorcentajeAplicado(v);
                }
            }
        }

        repo.save(e);
        log.info("DescuentoAplicado actualizado id={}", id);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("DescuentoAplicado eliminado id={}", id);
        return true;
    }
}
