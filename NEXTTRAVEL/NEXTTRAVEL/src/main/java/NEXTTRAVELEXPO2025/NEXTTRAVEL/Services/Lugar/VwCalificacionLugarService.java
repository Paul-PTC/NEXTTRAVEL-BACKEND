package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.VwCalificacionLugar;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.VwCalificacionLugarDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.VwCalificacionLugarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwCalificacionLugarService {

    private final VwCalificacionLugarRepository repo;

    private VwCalificacionLugarDTO toDTO(VwCalificacionLugar v) {
        return VwCalificacionLugarDTO.builder()
                .idCalificacionLugar(v.getIdCalificacionLugar())
                .nombreLugar(v.getNombreLugar())
                .cliente(v.getCliente())
                .puntuacion(v.getPuntuacion())
                .comentario(v.getComentario())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwCalificacionLugarDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }
    public Page<VwCalificacionLugarDTO> buscarPorLugar(String q, Pageable p) { return repo.findByNombreLugarContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwCalificacionLugarDTO> buscarPorCliente(String q, Pageable p) { return repo.findByClienteContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwCalificacionLugarDTO> buscarPorComentario(String q, Pageable p) { return repo.findByComentarioContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwCalificacionLugarDTO> buscarPorPuntuacionExacta(Integer n, Pageable p) { return repo.findByPuntuacion(n, p).map(this::toDTO); }
    public Page<VwCalificacionLugarDTO> buscarPorPuntuacionRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : 5;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntuacionBetween(from, to, p).map(this::toDTO);
    }
    public Page<VwCalificacionLugarDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        return repo.findByFechaBetween(d, h, p).map(this::toDTO);
    }
}
