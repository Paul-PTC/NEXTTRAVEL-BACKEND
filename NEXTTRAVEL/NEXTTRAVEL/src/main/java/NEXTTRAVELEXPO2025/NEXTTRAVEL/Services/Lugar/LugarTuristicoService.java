package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.LugarTuristicoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarTuristicoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LugarTuristicoService {

    private final LugarTuristicoRepository repo;

    // ===== Helpers =====
    private LugarTuristicoDTO toDTO(LugarTuristico e) {
        return LugarTuristicoDTO.builder()
                .nombreLugar(e.getNombreLugar())
                .descripcion(e.getDescripcion())
                .ubicacion(e.getUbicacion())
                .tipo(e.getTipo())
                .build();
    }

    private void apply(LugarTuristico e, LugarTuristicoDTO dto) {
        if (dto.getNombreLugar() != null) e.setNombreLugar(dto.getNombreLugar().trim());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getUbicacion() != null) e.setUbicacion(dto.getUbicacion());
        if (dto.getTipo() != null) e.setTipo(dto.getTipo());
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }

    // ===== Listado / Búsquedas =====
    public Page<LugarTuristicoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorNombre(String q, Pageable p) {
        return repo.findByNombreLugarContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorUbicacion(String q, Pageable p) {
        return repo.findByUbicacionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorTipo(String q, Pageable p) {
        return repo.findByTipoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<LugarTuristicoDTO> buscarPorDescripcion(String q, Pageable p) {
        return repo.findByDescripcionContainingIgnoreCase(q, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public LugarTuristicoDTO crear(@Valid LugarTuristicoDTO dto) {
        if (!notBlank(dto.getNombreLugar()))
            throw new IllegalArgumentException("nombreLugar es obligatorio.");

        if (repo.existsByNombreLugarIgnoreCase(dto.getNombreLugar()))
            throw new IllegalArgumentException("El nombreLugar ya existe.");

        LugarTuristico e = LugarTuristico.builder()
                .nombreLugar(dto.getNombreLugar().trim())
                .descripcion(dto.getDescripcion())
                .ubicacion(dto.getUbicacion())
                .tipo(dto.getTipo())
                .build();

        LugarTuristico guardado = repo.save(e);
        log.info("LugarTuristico creado: {}", guardado.getNombreLugar());
        return toDTO(guardado);
    }

    // ===== Actualizar por ID =====
    @Transactional
    public LugarTuristicoDTO actualizarPorId(Long id, @Valid LugarTuristicoDTO dto) {
        LugarTuristico e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró LugarTuristico con id: " + id));

        if (notBlank(dto.getNombreLugar())
                && !dto.getNombreLugar().equalsIgnoreCase(e.getNombreLugar())) {
            if (repo.existsByNombreLugarIgnoreCase(dto.getNombreLugar()))
                throw new IllegalArgumentException("El nuevo nombreLugar ya existe.");
        }
        apply(e, dto);

        LugarTuristico actualizado = repo.save(e);
        log.info("LugarTuristico actualizado: {} (id={})", actualizado.getNombreLugar(), id);
        return toDTO(actualizado);
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminarPorId(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("LugarTuristico eliminado id={}", id);
        return true;
    }
}
