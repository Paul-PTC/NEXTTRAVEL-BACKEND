package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.CalificacionLugar;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.CalificacionLugarDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.CalificacionLugarRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarTuristicoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
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
public class CalificacionLugarService {

    private final CalificacionLugarRepository repo;
    private final LugarTuristicoRepository lugarRepo;
    private final ClienteRepository clienteRepo;

    private void validatePuntuacion(Integer p) {
        if (p == null || p < 1 || p > 5) {
            throw new IllegalArgumentException("puntuacion debe estar entre 1 y 5.");
        }
    }

    // Crear
    @Transactional
    public Long crear(@Valid CalificacionLugarDTO dto) {
        validatePuntuacion(dto.getPuntuacion());

        LugarTuristico lugar = lugarRepo.findById(dto.getIdLugar())
                .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));

        Cliente cliente = clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));

        CalificacionLugar e = CalificacionLugar.builder()
                .lugar(lugar)
                .cliente(cliente)
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                .build();

        CalificacionLugar g = repo.save(e);
        log.info("CalificacionLugar creada id={} lugar={} cliente={}", g.getIdCalificacionLugar(), lugar.getIdLugar(), cliente.getDui());
        return g.getIdCalificacionLugar();
    }

    // Actualizar por ID
    @Transactional
    public void actualizar(Long id, @Valid CalificacionLugarDTO dto) {
        CalificacionLugar e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró CalificacionLugar con id: " + id));

        if (dto.getIdLugar() != null && (e.getLugar() == null || !dto.getIdLugar().equals(e.getLugar().getIdLugar()))) {
            LugarTuristico lugar = lugarRepo.findById(dto.getIdLugar())
                    .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));
            e.setLugar(lugar);
        }

        if (dto.getDuiCliente() != null && (e.getCliente() == null || !dto.getDuiCliente().equals(e.getCliente().getDui()))) {
            Cliente cliente = clienteRepo.findById(dto.getDuiCliente())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));
            e.setCliente(cliente);
        }

        if (dto.getPuntuacion() != null) {
            validatePuntuacion(dto.getPuntuacion());
            e.setPuntuacion(dto.getPuntuacion());
        }
        if (dto.getComentario() != null) e.setComentario(dto.getComentario());
        if (dto.getFecha() != null) e.setFecha(dto.getFecha());

        repo.save(e);
        log.info("CalificacionLugar actualizada id={}", id);
    }

    // Eliminar por ID
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("CalificacionLugar eliminada id={}", id);
        return true;
    }
}
