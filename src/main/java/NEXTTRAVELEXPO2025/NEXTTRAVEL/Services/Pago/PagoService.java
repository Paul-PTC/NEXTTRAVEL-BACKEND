package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Pago;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PagoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PagoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
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
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository repo;
    private final ReservaRepository reservaRepo;

    private PagoDTO toDTO(Pago e) {
        return PagoDTO.builder()
                .idReserva(e.getReserva() != null ? e.getReserva().getIdReserva() : null)
                .monto(e.getMonto())
                .metodo(e.getMetodo())
                .fecha(e.getFecha())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<PagoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<PagoDTO> buscarPorReserva(Long idReserva, Pageable p) {
        if (idReserva == null || idReserva <= 0) {
            throw new BadRequestException("El parámetro 'idReserva' no es válido.");
        }
        return repo.findByReserva_IdReserva(idReserva, p).map(this::toDTO);
    }

    public Page<PagoDTO> buscarPorMetodo(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new BadRequestException("El parámetro 'metodo' no puede estar vacío.");
        }
        return repo.findByMetodoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<PagoDTO> buscarPorMonto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : new BigDecimal("0.00");
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) {
            BigDecimal t = from; from = to; to = t;
        }
        return repo.findByMontoBetween(from, to, p).map(this::toDTO);
    }

    public Page<PagoDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        if (d == null || h == null) {
            throw new BadRequestException("El rango de fechas es obligatorio.");
        }
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid PagoDTO dto) {
        if (dto.getIdReserva() == null || dto.getIdReserva() <= 0) {
            throw new BadRequestException("El campo 'idReserva' es obligatorio y debe ser válido.");
        }

        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));

        if (dto.getMonto() == null || dto.getMonto().signum() < 0) {
            throw new BadRequestException("El campo 'monto' es obligatorio y debe ser mayor o igual a 0.");
        }

        try {
            Pago e = Pago.builder()
                    .reserva(r)
                    .monto(dto.getMonto())
                    .metodo(dto.getMetodo())
                    .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                    .build();

            Pago g = repo.save(e);
            log.info("Pago creado id={} reserva={} monto={}", g.getIdPago(), r.getIdReserva(), g.getMonto());
            return g.getIdPago();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Pago: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid PagoDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        Pago e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Pago con id: " + id));

        if (dto.getIdReserva() != null &&
                (e.getReserva() == null || !dto.getIdReserva().equals(e.getReserva().getIdReserva()))) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }

        if (dto.getMonto() != null) {
            if (dto.getMonto().signum() < 0) {
                throw new BadRequestException("El campo 'monto' debe ser mayor o igual a 0.");
            }
            e.setMonto(dto.getMonto());
        }
        if (dto.getMetodo() != null) e.setMetodo(dto.getMetodo());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        try {
            repo.save(e);
            log.info("Pago actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Pago: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("No existe Pago con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("Pago eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el Pago con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}
