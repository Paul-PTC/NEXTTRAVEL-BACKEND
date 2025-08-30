package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.VwEmpleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.VwEmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.VwEmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwEmpleadoService {

    private final VwEmpleadoRepository repo;

    private VwEmpleadoDTO toDTO(VwEmpleado v) {
        return VwEmpleadoDTO.builder()
                .dui(v.getDui())
                .nombre(v.getNombre())
                .correo(v.getCorreo())
                .telefono(v.getTelefono())
                .direccion(v.getDireccion())
                .rango(v.getRango())
                .salarioBase(v.getSalarioBase())
                .fechaContratacion(v.getFechaContratacion())
                .build();
    }

    public Page<VwEmpleadoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<VwEmpleadoDTO> buscarPorNombre(String q, Pageable p) { return repo.findByNombreContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEmpleadoDTO> buscarPorCorreo(String q, Pageable p) { return repo.findByCorreoContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEmpleadoDTO> buscarPorTelefono(String q, Pageable p) { return repo.findByTelefonoContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEmpleadoDTO> buscarPorDireccion(String q, Pageable p) { return repo.findByDireccionContainingIgnoreCase(q, p).map(this::toDTO); }
    public Page<VwEmpleadoDTO> buscarPorRango(String q, Pageable p) { return repo.findByRangoContainingIgnoreCase(q, p).map(this::toDTO); }

    public Page<VwEmpleadoDTO> buscarPorSalario(BigDecimal min, BigDecimal max, Pageable p) {
        BigDecimal from = (min != null) ? min : BigDecimal.ZERO;
        BigDecimal to   = (max != null) ? max : new BigDecimal("9999999999.99");
        if (from.compareTo(to) > 0) { BigDecimal t = from; from = to; to = t; }
        return repo.findBySalarioBaseBetween(from, to, p).map(this::toDTO);
    }
}
