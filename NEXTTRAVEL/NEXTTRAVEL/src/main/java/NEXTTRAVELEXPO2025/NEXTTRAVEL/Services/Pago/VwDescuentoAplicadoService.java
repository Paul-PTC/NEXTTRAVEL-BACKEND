package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.VwDescuentoAplicado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwDescuentoAplicadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.VwDescuentoAplicadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwDescuentoAplicadoService {

    private final VwDescuentoAplicadoRepository repo;

    private VwDescuentoAplicadoDTO toDTO(VwDescuentoAplicado v) {
        return VwDescuentoAplicadoDTO.builder()
                .idDescuentoAplicado(v.getIdDescuentoAplicado())
                .idReserva(v.getIdReserva())
                .cliente(v.getCliente())
                .lugar(v.getLugar())
                .tipoDescuento(v.getTipoDescuento())
                .promocion(v.getPromocion())
                .puntosUsados(v.getPuntosUsados())
                .porcentajeAplicado(v.getPorcentajeAplicado())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwDescuentoAplicadoDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwDescuentoAplicadoDTO> buscarPorCliente(String q, Pageable p) { return repo.findByClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwDescuentoAplicadoDTO> buscarPorLugar(String q, Pageable p) { return repo.findByLugarContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwDescuentoAplicadoDTO> buscarPorTipo(String tipo, Pageable p) { return repo.findByTipoDescuento(tipo.toUpperCase(), p).map(this::toDTO); }
    public Page<VwDescuentoAplicadoDTO> buscarPorPromocion(String q, Pageable p) { return repo.findByPromocionContainingIgnoreCase(q, p).map(this::toDTO); }

    public Page<VwDescuentoAplicadoDTO> buscarPorPorcentaje(Integer n, Pageable p) { return repo.findByPorcentajeAplicado(n, p).map(this::toDTO); }
    public Page<VwDescuentoAplicadoDTO> buscarPorPorcentajeRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 10;
        int to   = (max != null) ? max : 99;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPorcentajeAplicadoBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwDescuentoAplicadoDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }

    public Page<VwDescuentoAplicadoDTO> buscarPorReserva(Long idReserva, Pageable p) {
        return repo.findByIdReserva(idReserva, p).map(this::toDTO);
    }
}
