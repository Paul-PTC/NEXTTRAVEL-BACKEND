package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Calificacion;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.CalificacionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.CalificacionRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionRepository repo;
    private final ReservaRepository reservaRepo;

    private CalificacionDTO toDTO(Calificacion e) {
        return CalificacionDTO.builder()
                .idReserva(e.getReserva() != null ? e.getReserva().getIdReserva() : null)
                .puntuacion(e.getPuntuacion())
                .comentario(e.getComentario())
                .fecha(e.getFecha())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<CalificacionDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<CalificacionDTO> buscarPorComentario(String q, Pageable p) {
        return repo.findByComentarioContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<CalificacionDTO> buscarPorPuntuacion(Integer n, Pageable p) {
        return repo.findByPuntuacion(n, p).map(this::toDTO);
    }

    public Page<CalificacionDTO> buscarPorPuntuacionRango(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 1;
        int to   = (max != null) ? max : 5;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntuacionBetween(from, to, p).map(this::toDTO);
    }

    public Page<CalificacionDTO> buscarPorReserva(Long idReserva, Pageable p) {
        return repo.findByReserva_IdReserva(idReserva, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid CalificacionDTO dto) {
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5)
            throw new IllegalArgumentException("puntuacion debe estar entre 1 y 5.");

        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));

        Calificacion e = Calificacion.builder()
                .reserva(r)
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        Calificacion g = repo.save(e);
        log.info("Calificacion creada id={} reserva={}", g.getIdCalificacion(), r.getIdReserva());
        return g.getIdCalificacion();
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid CalificacionDTO dto) {
        Calificacion e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Calificacion con id: " + id));

        if (dto.getIdReserva() != null && (e.getReserva() == null ||
                !dto.getIdReserva().equals(e.getReserva().getIdReserva()))) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            e.setReserva(r);
        }
        if (dto.getPuntuacion() != null) {
            if (dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5)
                throw new IllegalArgumentException("puntuacion debe estar entre 1 y 5.");
            e.setPuntuacion(dto.getPuntuacion());
        }
        if (dto.getComentario() != null) e.setComentario(dto.getComentario());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        repo.save(e);
        log.info("Calificacion actualizada id={}", id);
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("Calificacion eliminada id={}", id);
        return true;
    }
}
