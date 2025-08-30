package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Reservas.Reserva;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.ReservaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarTuristicoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
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
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final ClienteRepository clienteRepo;
    private final LugarTuristicoRepository lugarRepo;

    private void apply(Reserva r, ReservaDTO d) {
        if (d.getFechaReserva() != null) r.setFechaReserva(d.getFechaReserva());
        if (d.getCantidadPersonas() != null) r.setCantidadPersonas(d.getCantidadPersonas());

        if (d.getPickupLat() != null) r.setPickupLat(d.getPickupLat());
        if (d.getPickupLng() != null) r.setPickupLng(d.getPickupLng());
        if (d.getPickupAddress() != null) r.setPickupAddress(d.getPickupAddress());

        if (d.getDropLat() != null) r.setDropLat(d.getDropLat());
        if (d.getDropLng() != null) r.setDropLng(d.getDropLng());
        if (d.getDropAddress() != null) r.setDropAddress(d.getDropAddress());

        if (d.getHoraRecogida() != null) r.setHoraRecogida(d.getHoraRecogida());
    }

    // Crear
    @Transactional
    public Long crear(@Valid ReservaDTO dto) {
        Cliente cliente = clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));
        LugarTuristico lugar = lugarRepo.findById(dto.getIdLugar())
                .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));

        Reserva r = Reserva.builder()
                .cliente(cliente)
                .lugar(lugar)
                .fechaReserva(dto.getFechaReserva() != null ? dto.getFechaReserva() : LocalDateTime.now())
                .cantidadPersonas(dto.getCantidadPersonas())
                .pickupLat(dto.getPickupLat())
                .pickupLng(dto.getPickupLng())
                .pickupAddress(dto.getPickupAddress())
                .dropLat(dto.getDropLat())
                .dropLng(dto.getDropLng())
                .dropAddress(dto.getDropAddress())
                .horaRecogida(dto.getHoraRecogida())
                .build();

        Reserva g = reservaRepo.save(r);
        log.info("Reserva creada id={} cliente={} lugar={}", g.getIdReserva(), cliente.getDui(), lugar.getIdLugar());
        return g.getIdReserva();
    }

    // Actualizar por ID
    @Transactional
    public void actualizar(Long id, @Valid ReservaDTO dto) {
        Reserva r = reservaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Reserva con id: " + id));

        if (dto.getDuiCliente() != null && (r.getCliente() == null ||
                !dto.getDuiCliente().equals(r.getCliente().getDui()))) {
            Cliente c = clienteRepo.findById(dto.getDuiCliente())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));
            r.setCliente(c);
        }

        if (dto.getIdLugar() != null && (r.getLugar() == null ||
                !dto.getIdLugar().equals(r.getLugar().getIdLugar()))) {
            LugarTuristico l = lugarRepo.findById(dto.getIdLugar())
                    .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));
            r.setLugar(l);
        }

        apply(r, dto);
        reservaRepo.save(r);
        log.info("Reserva actualizada id={}", id);
    }

    // Eliminar por ID
    @Transactional
    public boolean eliminar(Long id) {
        if (!reservaRepo.existsById(id)) return false;
        reservaRepo.deleteById(id);
        log.info("Reserva eliminada id={}", id);
        return true;
    }
}
