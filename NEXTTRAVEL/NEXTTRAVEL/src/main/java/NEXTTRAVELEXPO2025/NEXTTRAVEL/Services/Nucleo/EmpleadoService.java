package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Empleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.RangoEmpleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.EmpleadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.RangoEmpleadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
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
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepo;
    private final UsuarioRepository usuarioRepo;
    private final RangoEmpleadoRepository rangoRepo;

    private EmpleadoDTO toDTO(Empleado e) {
        return EmpleadoDTO.builder()
                .dui(e.getDui())
                .idUsuario(e.getUsuario() != null ? e.getUsuario().getIdUsuario() : null)
                .idRango(e.getRango() != null ? e.getRango().getIdRango() : null)
                .telefono(e.getTelefono())
                .direccion(e.getDireccion())
                .fechaContratacion(e.getFechaContratacion())
                .build();
    }

    private void apply(Empleado e, EmpleadoDTO dto) {
        if (dto.getTelefono() != null) e.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) e.setDireccion(dto.getDireccion());
        if (dto.getFechaContratacion() != null) e.setFechaContratacion(dto.getFechaContratacion());
    }

    // Crear
    @Transactional
    public EmpleadoDTO crear(@Valid EmpleadoDTO dto) {
        Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
        RangoEmpleado r = rangoRepo.findById(dto.getIdRango())
                .orElseThrow(() -> new EntityNotFoundException("No existe RangoEmpleado con id: " + dto.getIdRango()));

        Empleado e = Empleado.builder()
                .dui(dto.getDui())
                .usuario(u)
                .rango(r)
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .fechaContratacion(dto.getFechaContratacion() != null ? dto.getFechaContratacion() : LocalDateTime.now())
                .build();

        Empleado guardado = empleadoRepo.save(e);
        log.info("Empleado creado DUI={} (usuario={}, rango={})", guardado.getDui(), u.getIdUsuario(), r.getIdRango());
        return toDTO(guardado);
    }

    // Actualizar por DUI
    @Transactional
    public EmpleadoDTO actualizarPorDui(String dui, @Valid EmpleadoDTO dto) {
        Empleado e = empleadoRepo.findById(dui)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Empleado con DUI: " + dui));

        if (dto.getIdUsuario() != null && (e.getUsuario() == null ||
                !dto.getIdUsuario().equals(e.getUsuario().getIdUsuario()))) {
            Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
            e.setUsuario(u);
        }

        if (dto.getIdRango() != null && (e.getRango() == null ||
                !dto.getIdRango().equals(e.getRango().getIdRango()))) {
            RangoEmpleado r = rangoRepo.findById(dto.getIdRango())
                    .orElseThrow(() -> new EntityNotFoundException("No existe RangoEmpleado con id: " + dto.getIdRango()));
            e.setRango(r);
        }

        apply(e, dto);
        Empleado actualizado = empleadoRepo.save(e);
        log.info("Empleado actualizado DUI={}", dui);
        return toDTO(actualizado);
    }

    // Eliminar por DUI
    @Transactional
    public boolean eliminarPorDui(String dui) {
        if (!empleadoRepo.existsById(dui)) return false;
        empleadoRepo.deleteById(dui);
        log.info("Empleado eliminado DUI={}", dui);
        return true;
    }
}
