package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Ganancia;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GananciaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.GananciaRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (bruto == null || neto == null) {
            throw new BadRequestException("Los campos 'montoBruto' y 'montoNeto' son obligatorios.");
        }
        if (bruto.signum() < 0 || neto.signum() < 0) {
            throw new BadRequestException("Los montos no pueden ser negativos.");
        }
        if (neto.compareTo(bruto) > 0) {
            throw new BadRequestException("El 'montoNeto' no puede ser mayor que el 'montoBruto'.");
        }
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid GananciaDTO dto) {
        if (dto.getIdReserva() == null || dto.getIdReserva() <= 0) {
            throw new BadRequestException("El idReserva es obligatorio y debe ser válido.");
        }

        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));

        if (repo.existsByReserva_IdReserva(dto.getIdReserva())) {
            throw new ConflictException("Ya existe ganancia registrada para la reserva id=" + dto.getIdReserva());
        }

        validateAmounts(dto.getMontoBruto(), dto.getMontoNeto());

        try {
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
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Ganancia: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid GananciaDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        Ganancia e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Ganancia con id: " + id));

        if (dto.getIdReserva() != null &&
                (e.getReserva() == null || !dto.getIdReserva().equals(e.getReserva().getIdReserva()))) {
            // Respetar UNIQUE (idReserva)
            if (repo.existsByReserva_IdReserva(dto.getIdReserva())) {
                throw new ConflictException("Ya existe ganancia registrada para la reserva id=" + dto.getIdReserva());
            }
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        // previsualizar para validar
        BigDecimal bruto = dto.getMontoBruto() != null ? dto.getMontoBruto() : e.getMontoBruto();
        BigDecimal neto  = dto.getMontoNeto()  != null ? dto.getMontoNeto()  : e.getMontoNeto();
        validateAmounts(bruto, neto);

        if (dto.getMontoBruto() != null) e.setMontoBruto(dto.getMontoBruto());
        if (dto.getMontoNeto()  != null) e.setMontoNeto(dto.getMontoNeto());
        if (dto.getFecha()      != null) e.setFecha(dto.getFecha());

        try {
            repo.save(e);
            log.info("Ganancia actualizada id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Ganancia: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("No existe Ganancia con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("Ganancia eliminada id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar la Ganancia con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}
