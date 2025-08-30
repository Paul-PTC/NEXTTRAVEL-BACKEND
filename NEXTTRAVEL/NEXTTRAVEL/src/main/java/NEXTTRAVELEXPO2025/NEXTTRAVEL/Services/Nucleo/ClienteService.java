package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Cliente;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.ClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.ClienteRepository;
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
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final UsuarioRepository usuarioRepo;

    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .dui(c.getDui())
                .idUsuario(c.getUsuario() != null ? c.getUsuario().getIdUsuario() : null)
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }

    private void apply(Cliente c, ClienteDTO dto) {
        if (dto.getTelefono() != null) c.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) c.setDireccion(dto.getDireccion());
        if (dto.getFechaRegistro() != null) c.setFechaRegistro(dto.getFechaRegistro());
    }

    @Transactional
    public ClienteDTO crear(@Valid ClienteDTO dto) {
        Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));

        Cliente c = Cliente.builder()
                .dui(dto.getDui())
                .usuario(u)
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .fechaRegistro(dto.getFechaRegistro() != null ? dto.getFechaRegistro() : LocalDateTime.now())
                .build();

        Cliente guardado = clienteRepo.save(c);
        log.info("Cliente creado DUI={} (usuario={})", guardado.getDui(), u.getIdUsuario());
        return toDTO(guardado);
    }

    @Transactional
    public ClienteDTO actualizarPorDui(String dui, @Valid ClienteDTO dto) {
        Cliente c = clienteRepo.findById(dui)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Cliente con DUI: " + dui));

        if (dto.getIdUsuario() != null && (c.getUsuario() == null ||
                !dto.getIdUsuario().equals(c.getUsuario().getIdUsuario()))) {
            Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
            c.setUsuario(u);
        }

        apply(c, dto);
        Cliente actualizado = clienteRepo.save(c);
        log.info("Cliente actualizado DUI={}", dui);
        return toDTO(actualizado);
    }

    @Transactional
    public boolean eliminarPorDui(String dui) {
        if (!clienteRepo.existsById(dui)) return false;
        clienteRepo.deleteById(dui);
        log.info("Cliente eliminado DUI={}", dui);
        return true;
    }
}