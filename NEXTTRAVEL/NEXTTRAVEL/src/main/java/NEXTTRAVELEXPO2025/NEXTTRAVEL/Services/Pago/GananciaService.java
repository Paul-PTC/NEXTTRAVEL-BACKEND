package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Ganancia;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GananciaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.GananciaRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GananciaService {

    private final GananciaRepository repo;
    private final ReservaRepository reservaRepo;

    private void validateAmounts(BigDecimal bruto, BigDecimal neto) {
        if (bruto == null || neto == null)
            throw new IllegalArgumentException("montoBruto y montoNeto son obligatorios");
        if (bruto.signum() < 0 || neto.signum() < 0)
            throw new IllegalArgumentException("Los montos no pueden ser negativos");
        if (neto.compareTo(bruto) > 0)
            throw new IllegalArgumentException("montoNeto no puede ser mayor que montoBruto");
    }

    @Transactional
    public Long crear(@Valid GananciaDTO dto) {
        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));

        if (repo.existsByReserva_IdReserva(dto.getIdReserva()))
            throw new IllegalArgumentException("Ya existe ganancia para la reserva id=" + dto.getIdReserva());

        validateAmounts(dto.getMontoBruto(), dto.getMontoNeto());

        Ganancia e = Ganancia.builder()
                .reserva(r)
                .montoBruto(dto.getMontoBruto())
                .montoNeto(dto.getMontoNeto())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        Ganancia g = repo.save(e);
        log.info("Ganancia creada id={} reserva={} bruto={} neto={}",
                g.getIdGanancia(), r.getIdReserva(), g.getMontoBruto(), g.getMontoNeto());
        return g.getIdGanancia();
    }

    @Transactional
    public void actualizar(Long id, @Valid GananciaDTO dto) {
        Ganancia e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Ganancia con id: " + id));

        if (dto.getIdReserva() != null && (e.getReserva() == null ||
                !dto.getIdReserva().equals(e.getReserva().getIdReserva()))) {
            // Respetar UNIQUE (idReserva)
            if (repo.existsByReserva_IdReserva(dto.getIdReserva()))
                throw new IllegalArgumentException("Ya existe ganancia para la reserva id=" + dto.getIdReserva());
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        // previsualizar para validar
        java.math.BigDecimal bruto = dto.getMontoBruto() != null ? dto.getMontoBruto() : e.getMontoBruto();
        java.math.BigDecimal neto  = dto.getMontoNeto()  != null ? dto.getMontoNeto()  : e.getMontoNeto();
        validateAmounts(bruto, neto);

        if (dto.getMontoBruto() != null) e.setMontoBruto(dto.getMontoBruto());
        if (dto.getMontoNeto()  != null) e.setMontoNeto(dto.getMontoNeto());
        if (dto.getFecha()      != null) e.setFecha(dto.getFecha());

        repo.save(e);
        log.info("Ganancia actualizada id={}", id);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Ganancia eliminada id={}", id);
        return true;
    }
}
