package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.VwEstadoViaje;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.VwEstadoViajeDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.VwEstadoViajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwEstadoViajeService {

    private final VwEstadoViajeRepository repo;

    private VwEstadoViajeDTO toDTO(VwEstadoViaje v) {
        return VwEstadoViajeDTO.builder()
                .idEstadoViaje(v.getIdEstadoViaje())
                .idReserva(v.getIdReserva())
                .cliente(v.getCliente())
                .lugar(v.getLugar())
                .estado(v.getEstado())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwEstadoViajeDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwEstadoViajeDTO> buscarPorCliente(String q, Pageable p) { return repo.findByClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEstadoViajeDTO> buscarPorLugar(String q, Pageable p) { return repo.findByLugarContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEstadoViajeDTO> buscarPorEstado(String q, Pageable p) { return repo.findByEstadoContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEstadoViajeDTO> buscarPorReserva(Long idReserva, Pageable p) { return repo.findByIdReserva(idReserva, p).map(this::toDTO); }
    public Page<VwEstadoViajeDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) { return repo.findByFechaBetween(d, h, p).map(this::toDTO); }
}
