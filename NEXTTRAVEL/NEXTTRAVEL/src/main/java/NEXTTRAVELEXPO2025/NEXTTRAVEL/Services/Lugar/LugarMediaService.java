package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarMedia;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Lugar.LugarTuristico;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.LugarMediaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarMediaRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Lugar.LugarTuristicoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LugarMediaService {

    private final LugarMediaRepository repo;
    private final LugarTuristicoRepository lugarRepo;

    private String toFlag(Boolean primary) { return (primary != null && primary) ? "S" : "N"; }

    private void apply(LugarMedia e, LugarMediaDTO dto) {
        if (dto.getUrl() != null) e.setUrl(dto.getUrl());
        if (dto.getAltText() != null) e.setAltText(dto.getAltText());
        if (dto.getPrimary() != null) e.setIsPrimary(toFlag(dto.getPrimary()));
        if (dto.getPosition() != null) e.setPosition(dto.getPosition());
    }

    // Crear
    @Transactional
    public Long crear(@Valid LugarMediaDTO dto) {
        LugarTuristico lugar = lugarRepo.findById(dto.getIdLugar())
                .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));

        // Chequeo amistoso a la UNIQUE (idLugar, position)
        if (dto.getPosition() != null &&
                repo.existsByLugar_IdLugarAndPosition(dto.getIdLugar(), dto.getPosition())) {
            throw new IllegalArgumentException("Ya existe media en ese lugar con la misma posición.");
        }

        LugarMedia e = LugarMedia.builder()
                .lugar(lugar)
                .url(dto.getUrl())
                .altText(dto.getAltText())
                .isPrimary(toFlag(dto.getPrimary()))
                .position(dto.getPosition() != null ? dto.getPosition() : 1)
                .build();

        try {
            LugarMedia g = repo.save(e);
            log.info("LugarMedia creado id={} lugarId={}", g.getIdLugarMedia(), lugar.getIdLugar());
            return g.getIdLugarMedia();
        } catch (DataIntegrityViolationException ex) {
            // captura regex URL, unique (idLugar, position), etc.
            throw new IllegalArgumentException("Violación de integridad de datos: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // Actualizar por ID
    @Transactional
    public void actualizar(Long id, @Valid LugarMediaDTO dto) {
        LugarMedia e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró LugarMedia con id: " + id));

        if (dto.getIdLugar() != null && (e.getLugar() == null ||
                !dto.getIdLugar().equals(e.getLugar().getIdLugar()))) {
            LugarTuristico lugar = lugarRepo.findById(dto.getIdLugar())
                    .orElseThrow(() -> new EntityNotFoundException("No existe LugarTuristico con id: " + dto.getIdLugar()));
            e.setLugar(lugar);
        }

        // Chequear colisión de (idLugar, position) si cambia la position
        if (dto.getPosition() != null) {
            Long idLugar = (dto.getIdLugar() != null) ? dto.getIdLugar() : e.getLugar().getIdLugar();
            if (repo.existsByLugar_IdLugarAndPosition(idLugar, dto.getPosition())
                    && !dto.getPosition().equals(e.getPosition())) {
                throw new IllegalArgumentException("Ya existe media en ese lugar con la misma posición.");
            }
        }

        apply(e, dto);
        try {
            repo.save(e);
            log.info("LugarMedia actualizado id={}", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Violación de integridad de datos: " + ex.getMostSpecificCause().getMessage(), ex);
        }
    }

    // Eliminar por ID
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("LugarMedia eliminado id={}", id);
        return true;
    }
}
