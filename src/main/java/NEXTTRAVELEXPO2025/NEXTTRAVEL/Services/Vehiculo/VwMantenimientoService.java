package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Vehiculo.VwMantenimiento;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.VwMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Vehiculo.VwMantenimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VwMantenimientoService {

    private final VwMantenimientoRepository repo;

    private VwMantenimientoDTO toDTO(VwMantenimiento v) {
        return VwMantenimientoDTO.builder()
                .idMantenimiento(v.getIdMantenimiento())
                .placa(v.getPlaca())
                .modelo(v.getModelo())
                .tipoMantenimiento(v.getTipoMantenimiento())
                .descripcion(v.getDescripcion())
                .fecha(v.getFecha())
                .build();
    }

    public Page<VwMantenimientoDTO> listar(Pageable p) { return repo.findAll(p).map(this::toDTO); }

    public Page<VwMantenimientoDTO> buscarPorPlaca(String q, Pageable p) {
        return repo.findByPlacaContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwMantenimientoDTO> buscarPorModelo(String q, Pageable p) {
        return repo.findByModeloContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwMantenimientoDTO> buscarPorTipo(String q, Pageable p) {
        return repo.findByTipoMantenimientoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwMantenimientoDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<VwMantenimientoDTO> buscarPorFecha(LocalDateTime desde, LocalDateTime hasta, Pageable p) {
        return repo.findByFechaBetween(desde, hasta, p).map(this::toDTO);
    }
}
