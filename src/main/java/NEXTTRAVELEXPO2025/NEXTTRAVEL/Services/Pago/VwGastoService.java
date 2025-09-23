package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwGasto;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwGastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.VwGastoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwGastoService {

    private final VwGastoRepository repo;

    private VwGastoDTO toDTO(VwGasto v) {
        return VwGastoDTO.builder()
                .idGasto(v.getIdGasto())
                .tipoGasto(v.getTipoGasto())
                .monto(v.getMonto())
                .descripcion(v.getDescripcion())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwGastoDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwGastoDTO> buscarPorTipo(String q, Pageable p) {
        return repo.findByTipoGastoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwGastoDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwGastoDTO> buscarPorMonto(java.math.BigDecimal min, java.math.BigDecimal max, Pageable p) {
        java.math.BigDecimal from = (min != null) ? min : new java.math.BigDecimal("0.00");
        java.math.BigDecimal to   = (max != null) ? max : new java.math.BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) { java.math.BigDecimal t = from; from = to; to = t; }
        return repo.findByMontoBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwGastoDTO> buscarPorFecha(LocalDateTime desde, LocalDateTime hasta, Pageable p) {
        return repo.findByFechaBetween(desde, hasta, p).map(this::toDTO);
    }
}
