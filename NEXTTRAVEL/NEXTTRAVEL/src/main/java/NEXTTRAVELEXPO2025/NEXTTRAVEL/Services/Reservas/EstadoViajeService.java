package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Estado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.EstadoViaje;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.EstadoViajeDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.EstadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.EstadoViajeRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Reservas.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadoViajeService {

    private final EstadoViajeRepository repo;
    private final ReservaRepository reservaRepo;
    private final EstadoRepository estadoRepo;

    // Crear (agrega un nuevo "evento" de estado para la reserva)
    @Transactional
    public Long crear(@Valid EstadoViajeDTO dto) {
        Reserva r = reservaRepo.findById(dto.getIdReserva())
                .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
        Estado e = estadoRepo.findById(dto.getIdEstado())
                .orElseThrow(() -> new EntityNotFoundException("No existe Estado con id: " + dto.getIdEstado()));

        EstadoViaje ev = EstadoViaje.builder()
                .reserva(r)
                .estado(e)
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        EstadoViaje g = repo.save(ev);
        log.info("EstadoViaje creado id={} reserva={} estado={}", g.getIdEstadoViaje(), r.getIdReserva(), e.getIdEstado());
        return g.getIdEstadoViaje();
    }

    // Actualizar por ID (si necesitas corregir un evento)
    @Transactional
    public void actualizar(Long id, @Valid EstadoViajeDTO dto) {
        EstadoViaje ev = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró EstadoViaje con id: " + id));

        if (dto.getIdReserva() != null && (ev.getReserva() == null ||
                !dto.getIdReserva().equals(ev.getReserva().getIdReserva()))) {
            Reserva r = reservaRepo.findById(dto.getIdReserva())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Reserva con id: " + dto.getIdReserva()));
            ev.setReserva(r);
        }
        if (dto.getIdEstado() != null && (ev.getEstado() == null ||
                !dto.getIdEstado().equals(ev.getEstado().getIdEstado()))) {
            Estado e = estadoRepo.findById(dto.getIdEstado())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Estado con id: " + dto.getIdEstado()));
            ev.setEstado(e);
        }
        if (dto.getFecha() != null) ev.setFecha(dto.getFecha());

        repo.save(ev);
        log.info("EstadoViaje actualizado id={}", id);
    }

    // Eliminar por ID (borra el evento)
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("EstadoViaje eliminado id={}", id);
        return true;
    }
}
