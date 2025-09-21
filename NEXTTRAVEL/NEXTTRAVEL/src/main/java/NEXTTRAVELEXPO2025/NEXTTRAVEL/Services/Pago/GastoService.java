package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Gasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.TipoGasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoTipoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.GastoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.TipoGastoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository repo;
    private final TipoGastoRepository tipoRepo;

    private void validateMonto(BigDecimal v) {
        if (v == null) {
            throw new BadRequestException("El campo 'monto' es obligatorio.");
        }
        if (v.signum() < 0) {
            throw new BadRequestException("El campo 'monto' debe ser mayor o igual a 0.");
        }
    }

    // ===== Listar solo tipo de gasto =====
    public List<GastoTipoDTO> listarTipos() {
        return repo.findAll().stream()
                .map(g -> new GastoTipoDTO(
                        g.getIdGasto(),
                        g.getTipoGasto() != null ? g.getTipoGasto().getNombreTipo() : null
                ))
                .collect(Collectors.toList());
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid GastoDTO dto) {
        if (dto.getIdTipoGasto() == null || dto.getIdTipoGasto() <= 0) {
            throw new BadRequestException("El campo 'idTipoGasto' es obligatorio y debe ser válido.");
        }

        TipoGasto tg = tipoRepo.findById(dto.getIdTipoGasto())
                .orElseThrow(() -> new ResourceNotFoundException("No existe TipoGasto con id: " + dto.getIdTipoGasto()));

        validateMonto(dto.getMonto());

        try {
            Gasto e = Gasto.builder()
                    .tipoGasto(tg)
                    .monto(dto.getMonto())
                    .descripcion(dto.getDescripcion())
                    .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                    .build();

            Gasto g = repo.save(e);
            log.info("Gasto creado id={} tipo={} monto={}", g.getIdGasto(), tg.getNombreTipo(), g.getMonto());
            return g.getIdGasto();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Gasto: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar =====
    @Transactional
    public void actualizar(Long id, @Valid GastoDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id proporcionado no es válido.");
        }

        Gasto e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Gasto con id: " + id));

        if (dto.getIdTipoGasto() != null &&
                (e.getTipoGasto() == null || !dto.getIdTipoGasto().equals(e.getTipoGasto().getIdTipoGasto()))) {
            TipoGasto tg = tipoRepo.findById(dto.getIdTipoGasto())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe TipoGasto con id: " + dto.getIdTipoGasto()));
            e.setTipoGasto(tg);
        }

        if (dto.getMonto() != null) {
            validateMonto(dto.getMonto());
            e.setMonto(dto.getMonto());
        }
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        try {
            repo.save(e);
            log.info("Gasto actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Gasto: "
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
            throw new ResourceNotFoundException("No existe Gasto con id: " + id);
        }

        try {
            repo.deleteById(id);
            log.info("Gasto eliminado id={}", id);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el Gasto con id " + id
                    + " porque tiene dependencias activas.");
        }
    }
}
