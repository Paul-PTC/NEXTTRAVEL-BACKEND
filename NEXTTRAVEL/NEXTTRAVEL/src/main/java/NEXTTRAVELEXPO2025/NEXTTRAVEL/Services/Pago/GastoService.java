package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.Gasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.TipoGasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoTipoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.GastoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.TipoGastoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (v == null || v.signum() < 0) throw new IllegalArgumentException("monto debe ser >= 0");
    }

    // listar solo tipo de gasto
    public List<GastoTipoDTO> listarTipos() {
        return repo.findAll().stream()
                .map(g -> new GastoTipoDTO(
                        g.getIdGasto(),
                        g.getTipoGasto() != null ? g.getTipoGasto().getNombreTipo() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long crear(@Valid GastoDTO dto) {
        TipoGasto tg = tipoRepo.findById(dto.getIdTipoGasto())
                .orElseThrow(() -> new EntityNotFoundException("No existe TipoGasto con id: " + dto.getIdTipoGasto()));

        validateMonto(dto.getMonto());

        Gasto e = Gasto.builder()
                .tipoGasto(tg)
                .monto(dto.getMonto())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        Gasto g = repo.save(e);
        log.info("Gasto creado id={} tipo={} monto={}", g.getIdGasto(), tg.getNombreTipo(), g.getMonto());
        return g.getIdGasto();
    }

    @Transactional
    public void actualizar(Long id, @Valid GastoDTO dto) {
        Gasto e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Gasto con id: " + id));

        if (dto.getIdTipoGasto() != null &&
                (e.getTipoGasto() == null || !dto.getIdTipoGasto().equals(e.getTipoGasto().getIdTipoGasto()))) {
            TipoGasto tg = tipoRepo.findById(dto.getIdTipoGasto())
                    .orElseThrow(() -> new EntityNotFoundException("No existe TipoGasto con id: " + dto.getIdTipoGasto()));
            e.setTipoGasto(tg);
        }
        if (dto.getMonto() != null) {
            validateMonto(dto.getMonto());
            e.setMonto(dto.getMonto());
        }
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        repo.save(e);
        log.info("Gasto actualizado id={}", id);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Gasto eliminado id={}", id);
        return true;
    }
}
