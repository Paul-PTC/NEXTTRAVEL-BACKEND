package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Empleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.RangoEmpleado;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.BadRequestException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ConflictException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Exeptions.ResourceNotFoundException;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.EmpleadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.RangoEmpleadoRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    // ===== Crear =====
    @Transactional
    public EmpleadoDTO crear(@Valid EmpleadoDTO dto) {
        if (dto.getDui() == null || dto.getDui().isBlank()) {
            throw new BadRequestException("El campo 'DUI' es obligatorio.");
        }
        if (empleadoRepo.existsById(dto.getDui())) {
            throw new ConflictException("Ya existe un empleado con el DUI: " + dto.getDui());
        }

        Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));

        RangoEmpleado r = rangoRepo.findById(dto.getIdRango())
                .orElseThrow(() -> new ResourceNotFoundException("No existe RangoEmpleado con id: " + dto.getIdRango()));

        try {
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
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al crear Empleado: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Actualizar por DUI =====
    @Transactional
    public EmpleadoDTO actualizarPorDui(String dui, @Valid EmpleadoDTO dto) {
        if (dui == null || dui.isBlank()) {
            throw new BadRequestException("El DUI proporcionado no puede estar vacío.");
        }

        Empleado e = empleadoRepo.findById(dui)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró Empleado con DUI: " + dui));

        if (dto.getIdUsuario() != null &&
                (e.getUsuario() == null || !dto.getIdUsuario().equals(e.getUsuario().getIdUsuario()))) {
            Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
            e.setUsuario(u);
        }

        if (dto.getIdRango() != null &&
                (e.getRango() == null || !dto.getIdRango().equals(e.getRango().getIdRango()))) {
            RangoEmpleado r = rangoRepo.findById(dto.getIdRango())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe RangoEmpleado con id: " + dto.getIdRango()));
            e.setRango(r);
        }

        apply(e, dto);

        try {
            Empleado actualizado = empleadoRepo.save(e);
            log.info("Empleado actualizado DUI={}", dui);
            return toDTO(actualizado);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Error de integridad al actualizar Empleado: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    // ===== Eliminar por DUI =====
    @Transactional
    public boolean eliminarPorDui(String dui) {
        if (dui == null || dui.isBlank()) {
            throw new BadRequestException("El DUI proporcionado no puede estar vacío.");
        }

        if (!empleadoRepo.existsById(dui)) {
            throw new ResourceNotFoundException("No existe Empleado con DUI: " + dui);
        }

        try {
            empleadoRepo.deleteById(dui);
            log.info("Empleado eliminado DUI={}", dui);
            return true;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("No se puede eliminar el Empleado con DUI " + dui
                    + " porque tiene dependencias activas.");
        }
    }
}
