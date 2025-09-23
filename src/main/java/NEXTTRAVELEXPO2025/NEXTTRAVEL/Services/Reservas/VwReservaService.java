package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.VwReserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.VwReservaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.VwReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwReservaService {

    private final VwReservaRepository repo;

    private VwReservaDTO toDTO(VwReserva v) {
        return VwReservaDTO.builder()
                .idReserva(v.getIdReserva())
                .duiCliente(v.getDuiCliente())
                .nombreCliente(v.getNombreCliente())
                .lugar(v.getLugar())
                .fechaReserva(v.getFechaReserva())
                .cantidadPersonas(v.getCantidadPersonas())
                .estadoActual(v.getEstadoActual())
                .build();
    }

    public Page<VwReservaDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwReservaDTO> buscarPorNombreCliente(String q, Pageable p) { return repo.findByNombreClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwReservaDTO> buscarPorDui(String q, Pageable p) { return repo.findByDuiClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwReservaDTO> buscarPorLugar(String q, Pageable p) { return repo.findByLugarContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwReservaDTO> buscarPorEstado(String q, Pageable p) { return repo.findByEstadoActualContainingIgnoreCase(q, p).map(this::toDTO); }

    public Page<VwReservaDTO> buscarPorCantidad(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByCantidadPersonasBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwReservaDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        return repo.findByFechaReservaBetween(d, h, p).map(this::toDTO);
    }
}
