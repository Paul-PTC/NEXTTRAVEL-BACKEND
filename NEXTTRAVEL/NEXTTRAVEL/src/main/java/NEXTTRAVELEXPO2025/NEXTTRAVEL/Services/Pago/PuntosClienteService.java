package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.PuntosCliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PuntosClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PuntosClienteRepository;
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
public class PuntosClienteService {

    private final PuntosClienteRepository repo;
    private final ClienteRepository clienteRepo;

    private PuntosClienteDTO toDTO(PuntosCliente e) {
        return PuntosClienteDTO.builder()
                .duiCliente(e.getDuiCliente())
                .puntos(e.getPuntos())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<PuntosClienteDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<PuntosClienteDTO> buscarPorDui(String q, Pageable p) {
        return repo.findByDuiClienteContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<PuntosClienteDTO> buscarPorPuntos(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 0;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosBetween(from, to, p).map(this::toDTO);
    }

    public Page<PuntosClienteDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        return repo.findByFechaActualizacionBetween(d, h, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public PuntosClienteDTO crear(@Valid PuntosClienteDTO dto) {
        // validar existencia del cliente (FK)
        clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));

        if (repo.existsById(dto.getDuiCliente())) {
            throw new IllegalArgumentException("Ya existen puntos para el DUI: " + dto.getDuiCliente());
        }

        PuntosCliente e = PuntosCliente.builder()
                .duiCliente(dto.getDuiCliente())
                .puntos(dto.getPuntos())
                .build();

        PuntosCliente g = repo.save(e);
        log.info("PuntosCliente creado DUI={} puntos={}", g.getDuiCliente(), g.getPuntos());
        return toDTO(g);
    }

    // ===== Actualizar por DUI =====
    @Transactional
    public PuntosClienteDTO actualizar(String dui, @Valid PuntosClienteDTO dto) {
        PuntosCliente e = repo.findById(dui)
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron puntos para DUI: " + dui));

        if (dto.getPuntos() != null) {
            if (dto.getPuntos() < 0) throw new IllegalArgumentException("puntos debe ser >= 0");
            e.setPuntos(dto.getPuntos());
        }
        PuntosCliente g = repo.save(e);
        log.info("PuntosCliente actualizado DUI={} puntos={}", g.getDuiCliente(), g.getPuntos());
        return toDTO(g);
    }

    // ===== Eliminar por DUI =====
    @Transactional
    public boolean eliminar(String dui) {
        if (!repo.existsById(dui)) return false;
        repo.deleteById(dui);
        log.info("PuntosCliente eliminado DUI={}", dui);
        return true;
    }
}
