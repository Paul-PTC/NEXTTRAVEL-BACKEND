package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwGanancia;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwGananciaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.VwGananciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwGananciaService {

    private final VwGananciaRepository repo;

    private VwGananciaDTO toDTO(VwGanancia v) {
        return VwGananciaDTO.builder()
                .idGanancia(v.getIdGanancia())
                .idReserva(v.getIdReserva())
                .cliente(v.getCliente())
                .lugar(v.getLugar())
                .montoBruto(v.getMontoBruto())
                .montoNeto(v.getMontoNeto())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwGananciaDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwGananciaDTO> buscarPorCliente(String q, Pageable p) { return repo.findByClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwGananciaDTO> buscarPorLugar(String q, Pageable p) { return repo.findByLugarContainingIgnoreCase(q, p).map(this::toDTO); }

    public Page<VwGananciaDTO> buscarPorMontoBruto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : BigDecimal.ZERO;
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) { BigDecimal t = from; from = to; to = t; }
        return repo.findByMontoBrutoBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwGananciaDTO> buscarPorMontoNeto(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : BigDecimal.ZERO;
        BigDecimal to   = (max != null) ? max : new BigDecimal("99999999.99");
        if (from.compareTo(to) > 0) { BigDecimal t = from; from = to; to = t; }
        return repo.findByMontoNetoBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwGananciaDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }

    public Page<VwGananciaDTO> buscarPorReserva(Long idReserva, Pageable p) {
        return repo.findByIdReserva(idReserva, p).map(this::toDTO);
    }
}
