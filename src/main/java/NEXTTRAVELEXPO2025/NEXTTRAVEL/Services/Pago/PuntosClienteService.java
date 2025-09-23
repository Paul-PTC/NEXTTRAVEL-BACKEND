package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Pago.PuntosCliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PuntosClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Pago.PuntosClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (q == null || q.isBlank()) {
            throw new IllegalArgumentException("El DUI de búsqueda no puede estar vacío.");
        }
        return repo.findByDuiClienteContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<PuntosClienteDTO> buscarPorPuntos(Integer min, Integer max, Pageable p) {
        int from = (min != null) ? min : 0;
        int to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosBetween(from, to, p).map(this::toDTO);
    }

    public Page<PuntosClienteDTO> buscarPorFecha(LocalDateTime d, LocalDateTime h, Pageable p) {
        if (d == null || h == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para la búsqueda.");
        }
        if (h.isBefore(d)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        return repo.findByFechaActualizacionBetween(d, h, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public PuntosClienteDTO crear(@Valid PuntosClienteDTO dto) {
        if (dto.getDuiCliente() == null || dto.getDuiCliente().isBlank()) {
            throw new IllegalArgumentException("El DUI del cliente es obligatorio.");
        }
        if (dto.getPuntos() != null && dto.getPuntos() < 0) {
            throw new IllegalArgumentException("Los puntos iniciales no pueden ser negativos.");
        }

        // validar existencia del cliente (FK)
        clienteRepo.findById(dto.getDuiCliente())
                .orElseThrow(() -> new EntityNotFoundException("No existe Cliente con DUI: " + dto.getDuiCliente()));

        if (repo.existsById(dto.getDuiCliente())) {
            throw new IllegalArgumentException("Ya existen puntos asignados para el DUI: " + dto.getDuiCliente());
        }

        try {
            PuntosCliente e = PuntosCliente.builder()
                    .duiCliente(dto.getDuiCliente())
                    .puntos(dto.getPuntos() != null ? dto.getPuntos() : 0)
                    .build();

            PuntosCliente g = repo.save(e);
            log.info("PuntosCliente creado DUI={} puntos={}", g.getDuiCliente(), g.getPuntos());
            return toDTO(g);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al crear PuntosCliente: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Actualizar por DUI =====
    @Transactional
    public PuntosClienteDTO actualizar(String dui, @Valid PuntosClienteDTO dto) {
        if (dui == null || dui.isBlank()) {
            throw new IllegalArgumentException("El DUI proporcionado no es válido.");
        }

        PuntosCliente e = repo.findById(dui)
                .orElseThrow(() -> new EntityNotFoundException("No se encontraron puntos para DUI: " + dui));

        if (dto.getPuntos() != null) {
            if (dto.getPuntos() < 0) {
                throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
            }
            e.setPuntos(dto.getPuntos());
        }

        try {
            PuntosCliente g = repo.save(e);
            log.info("PuntosCliente actualizado DUI={} puntos={}", g.getDuiCliente(), g.getPuntos());
            return toDTO(g);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Error de integridad al actualizar PuntosCliente: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // ===== Eliminar por DUI =====
    @Transactional
    public boolean eliminar(String dui) {
        if (dui == null || dui.isBlank()) {
            throw new IllegalArgumentException("El DUI proporcionado no es válido.");
        }

        if (!repo.existsById(dui)) {
            throw new EntityNotFoundException("No existen puntos asociados al DUI: " + dui);
        }

        try {
            repo.deleteById(dui);
            log.info("PuntosCliente eliminado DUI={}", dui);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("No se puede eliminar PuntosCliente con DUI=" + dui + " debido a dependencias.", ex);
        }
    }
}
