package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.LugarMediaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.VwLugarMediaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar.LugarMediaService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar.VwLugarMediaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/lugarmedia")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://127.0.0.1:5501","http://localhost:5501",
        "http://127.0.0.1:5500","http://localhost:5500",
        "http://127.0.0.1:5502","http://localhost:5502"
})
public class LugarMediaController {

    private final LugarMediaService mediaService; // tabla
    private final VwLugarMediaService vwService;  // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/media/listar")
    public ResponseEntity<Page<VwLugarMediaDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {}
    @GetMapping("/media/buscar/{lugar}")
    public ResponseEntity<Page<VwLugarMediaDTO>> buscarPorLugar(
            @PathVariable String lugar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(lugar, buildPageable(page, size, sort)));
    }

    @GetMapping("/media/buscar/url/{q}")
    public ResponseEntity<Page<VwLugarMediaDTO>> buscarPorUrl(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorUrl(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/media/buscar/alt/{q}")
    public ResponseEntity<Page<VwLugarMediaDTO>> buscarPorAlt(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorAlt(q, buildPageable(page, size, sort)));
    }

    // 'S' o 'N'
    @GetMapping("/media/buscar/primary/{flag}")
    public ResponseEntity<Page<VwLugarMediaDTO>> buscarPorPrimary(
            @PathVariable String flag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPrimary(flag, buildPageable(page, size, sort)));
    }

    @GetMapping("/media/buscar/position/{pos}")
    public ResponseEntity<Page<VwLugarMediaDTO>> buscarPorPosition(
            @PathVariable Integer pos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPosition(pos, buildPageable(page, size, sort)));
    }

    // /api/lugarmedia/media/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/media/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        try {
            LocalDateTime d = LocalDateTime.parse(desde);
            LocalDateTime h = LocalDateTime.parse(hasta);
            return ResponseEntity.ok(vwService.buscarPorFecha(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato de fecha inválido. Usa ISO-8601: 2025-01-31T12:34:56"
            ));
        }
    }

    // ===== POST/PUT/DELETE (tabla) =====
    @PostMapping("/media")
    public ResponseEntity<?> crear(@Valid @RequestBody LugarMediaDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            result.getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", "Datos para insercion invalidos",
                    "errors", fieldErrors
            ));
        }
        try {
            Long id = mediaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "idLugarMedia", id
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "errorType", "NOT_FOUND",
                    "message", ex.getMessage()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear LugarMedia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la media",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/media/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody LugarMediaDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            mediaService.actualizar(id, dto);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "LugarMedia no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar LugarMedia {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar media", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/media/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = mediaService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "LugarMedia no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "LugarMedia eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar LugarMedia {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar media", "detalle", e.getMessage()));
        }
    }
}
