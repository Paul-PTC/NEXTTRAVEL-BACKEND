package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Seguridad;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Seguridad.CodigoVerificacion;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Seguridad.CodigoVerificacionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Nucleo.UsuarioRepository;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Repositories.Seguridad.CodigoVerificacionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodigoVerificacionService {

    private final CodigoVerificacionRepository repo;
    private final UsuarioRepository usuarioRepo;

    private CodigoVerificacionDTO toDTO(CodigoVerificacion e) {
        return CodigoVerificacionDTO.builder()
                .idCodigo(e.getIdCodigo())
                .idUsuario(e.getUsuario() != null ? e.getUsuario().getIdUsuario() : null)
                .codigo(e.getCodigo())
                .fechaGeneracion(e.getFechaGeneracion())
                .validoHasta(e.getValidoHasta())
                .build();
    }

    // ===== Listado / Búsquedas =====
    public Page<CodigoVerificacionDTO> listar(Pageable p) {
        return repo.findAll(p).map(this::toDTO);
    }

    public Page<CodigoVerificacionDTO> buscarPorUsuario(Long idUsuario, Pageable p) {
        return repo.findByUsuario_IdUsuario(idUsuario, p).map(this::toDTO);
    }

    public Page<CodigoVerificacionDTO> buscarPorCodigo(String q, Pageable p) {
        return repo.findByCodigoContainingIgnoreCase(q, p).map(this::toDTO);
    }

    public Page<CodigoVerificacionDTO> buscarPorGeneracion(LocalDateTime desde, LocalDateTime hasta, Pageable p) {
        return repo.findByFechaGeneracionBetween(desde, hasta, p).map(this::toDTO);
    }

    public Page<CodigoVerificacionDTO> buscarPorValidez(LocalDate desde, LocalDate hasta, Pageable p) {
        return repo.findByValidoHastaBetween(desde, hasta, p).map(this::toDTO);
    }

    public Page<CodigoVerificacionDTO> buscarVigentes(LocalDate fecha, Pageable p) {
        return repo.findByValidoHastaGreaterThanEqual(fecha, p).map(this::toDTO);
    }

    // ===== Crear =====
    @Transactional
    public Long crear(@Valid CodigoVerificacionDTO dto) {
        Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));

        String code = dto.getCodigo().trim();
        if (code.length() > 10) throw new IllegalArgumentException("codigo no debe exceder 10 caracteres");
        // normalizar (opcional)
        code = code.toUpperCase();

        CodigoVerificacion e = CodigoVerificacion.builder()
                .usuario(u)
                .codigo(code)
                .fechaGeneracion(dto.getFechaGeneracion() != null ? dto.getFechaGeneracion() : LocalDateTime.now())
                .validoHasta(dto.getValidoHasta()) // puede ser null
                .build();

        CodigoVerificacion g = repo.save(e);
        log.info("CodigoVerificacion creado id={} usuario={} codigo={}", g.getIdCodigo(), u.getIdUsuario(), g.getCodigo());
        return g.getIdCodigo();
    }

    // ===== Actualizar por ID =====
    @Transactional
    public void actualizar(Long id, @Valid CodigoVerificacionDTO dto) {
        CodigoVerificacion e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró CodigoVerificacion con id: " + id));

        if (dto.getIdUsuario() != null &&
                (e.getUsuario() == null || !dto.getIdUsuario().equals(e.getUsuario().getIdUsuario()))) {
            Usuario u = usuarioRepo.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("No existe Usuario con id: " + dto.getIdUsuario()));
            e.setUsuario(u);
        }

        if (dto.getCodigo() != null) {
            String code = dto.getCodigo().trim();
            if (code.length() > 10) throw new IllegalArgumentException("codigo no debe exceder 10 caracteres");
            e.setCodigo(code.toUpperCase());
        }
        if (dto.getFechaGeneracion() != null) e.setFechaGeneracion(dto.getFechaGeneracion());
        if (dto.getValidoHasta() != null) e.setValidoHasta(dto.getValidoHasta());

        repo.save(e);
        log.info("CodigoVerificacion actualizado id={}", id);
    }

    // ===== Eliminar por ID =====
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        log.info("CodigoVerificacion eliminado id={}", id);
        return true;
    }
}
