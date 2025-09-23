package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.VwCliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.VwClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.VwClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwClienteService {

    private final VwClienteRepository repo;

    private VwClienteDTO toDTO(VwCliente v) {
        return VwClienteDTO.builder()
                .dui(v.getDui())
                .nombre(v.getNombre())
                .correo(v.getCorreo())
                .telefono(v.getTelefono())
                .direccion(v.getDireccion())
                .fechaRegistro(v.getFechaRegistro())
                .puntosActuales(v.getPuntosActuales())
                .idUsuario(v.getIdUsuario())
                .build();
    }

    public Page<VwClienteDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    // --- Métodos de Búsqueda ---

    public VwClienteDTO buscarPorDui(String dui) {
        return repo.findById(dui)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cliente con DUI " + dui + " no encontrado"));
    }

    public Page<VwClienteDTO> buscarPorNombre(String q, Pageable p) {
        return repo.findByNombreContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwClienteDTO> buscarPorCorreo(String q, Pageable p) {
        return repo.findByCorreoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwClienteDTO> buscarPorTelefono(String q, Pageable p) {
        return repo.findByTelefonoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwClienteDTO> buscarPorDireccion(String q, Pageable p) {
        return repo.findByDireccionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwClienteDTO> buscarPorPuntos(Integer min, Integer max, Pageable p) {
        Integer from = (min != null) ? min : 0;
        Integer to   = (max != null) ? max : Integer.MAX_VALUE;
        if (from > to) { int t = from; from = to; to = t; }
        return repo.findByPuntosActualesBetween(from, to, p).map(this::toDTO);
    }

    public Page<VwClienteDTO> buscarPorFecha(LocalDateTime desde, LocalDateTime hasta, Pageable p) {
        return repo.findByFechaRegistroBetween(desde, hasta, p).map(this::toDTO);
    }
}