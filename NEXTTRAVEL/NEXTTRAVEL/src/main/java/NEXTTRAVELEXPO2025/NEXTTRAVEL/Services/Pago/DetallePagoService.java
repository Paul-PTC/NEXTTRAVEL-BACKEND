package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.DetallePago;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Pago;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.DetallePagoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.DetallePagoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PagoRepository;
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
public class DetallePagoService {

    private final DetallePagoRepository repo;
    private final PagoRepository pagoRepo;

    private DetallePagoDTO toDTO(DetallePago e) {
        return DetallePagoDTO.builder()
                .idPago(e.getPago() != null ? e.getPago().getIdPago() : null)
                .descripcion(e.getDescripcion())
                .monto(e.getMonto())
                .build();
    }

    private void validateMonto(BigDecimal monto) {
        if (monto == null) {
            throw new BadRequestException("El campo 'monto' es obligatorio.");
        }
        if (monto.signum() < 0) {
            throw new BadRequestException("El campo 'monto' debe ser mayor o igual a 0.");
        }
    }

    // ===== Listado / Búsquedas =====
    public Page<DetallePagoDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<DetallePagoDTO> buscarPorPago(Long idPago, Pageable p) {
        if (idPago == null || idPago <= 0) {
            throw new BadRequestException("El idPago proporcionado no es válido.");
        }
        return repo.findByPago_IdPago(idPago, p).map(this::toDTO);
    }

    public Page<DetallePagoDTO> buscarPorDescripcion(String q, Pageable p) {
        if (q == null || q.isBlank()) {
            throw new BadRequestException("El parámetro 'descripcion' no puede estar vacío.");
        }
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<DetallePagoDTO> buscarPorMonto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : new BigDecimal("0.00");
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) {
            BigDecimal t = from; from = to; to = t;
        }
        return repo.findByMontoBetween(from, to, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid DetallePagoDTO dto) {
        Pago pago = pagoRepo.findById(dto.getIdPago())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Pago con id: " + dto.getIdPago()));

        validateMonto(dto.getMonto());

        try {
            DetallePago e = DetallePago.builder()
                    .pago(pago)
                    .descripcion(dto.getDescripcion())
                    .monto(dto.getMonto())
                    .build();

            DetallePago g = repo.save(e);
            log.info("DetallePago creado id={} pago={} monto={}", g.getIdDetallePago(), pago.getIdPago(), g.getMonto());
            return g.getIdDetallePago();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear DetallePago: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid DetallePagoDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        DetallePago e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró DetallePago con id: " + id));

        if (dto.getIdPago() != null &&
                (e.getPago() == null || !dto.getIdPago().equals(e.getPago().getIdPago()))) {
            Pago pago = pagoRepo.findById(dto.getIdPago())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Pago con id: " + dto.getIdPago()));
            e.setPago(pago);
        }

        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getMonto() != null) {
            validateMonto(dto.getMonto());
            e.setMonto(dto.getMonto());
        }

        try {
            repo.save(e);
            log.info("DetallePago actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar DetallePago: "
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
            throw new ResourceNotFoundException("No existe DetallePago con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("DetallePago eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el DetallePago con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}