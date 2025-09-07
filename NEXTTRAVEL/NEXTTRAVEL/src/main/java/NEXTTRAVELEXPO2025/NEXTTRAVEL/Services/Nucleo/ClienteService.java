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
import java.util.Optional;

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
                .puntosactuales(c.getPuntosactuales())
                .build();
    }

    private void apply(Cliente c, ClienteDTO dto) {
        if (dto.getTelefono() != null) c.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) c.setDireccion(dto.getDireccion());
        if (dto.getPuntosactuales() != null) c.setPuntosactuales(dto.getPuntosactuales());
    }

    @Transactional
    public ClienteDTO crear(@Valid ClienteDTO dto) {
        Usuario u = null;
        if (dto.getIdUsuario() != null) {
            u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
        }

        if (clienteRepo.existsById(dto.getDui())) {
            throw new IllegalArgumentException("Ya existe un cliente con el DUI: " + dto.getDui());
        }

        Cliente c = Cliente.builder()
                .dui(dto.getDui())
                .usuario(u)
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .puntosactuales(dto.getPuntosactuales() != null ? dto.getPuntosactuales() : 0)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Cliente guardado = clienteRepo.save(c);
        return toDTO(guardado);
    }

    @Transactional
    public ClienteDTO actualizarPorDui(String dui, @Valid ClienteDTO dto) {
        Cliente c = clienteRepo.findById(dui)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Cliente con DUI: " + dui));

        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
            c.setUsuario(u);
        } else {
            c.setUsuario(null);
        }

        if (dto.getPuntosactuales() != null) {
            c.setPuntosactuales(dto.getPuntosactuales());
        }

        if (dto.getTelefono() != null) c.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) c.setDireccion(dto.getDireccion());

        Cliente actualizado = clienteRepo.save(c);
        return toDTO(actualizado);
    }

    @Transactional
    public boolean eliminarPorDui(String dui) {
        if (!clienteRepo.existsById(dui)) return false;
        clienteRepo.deleteById(dui);
        return true;
    }
}
