package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.CalificacionLugarDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.VwCalificacionLugarDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar.CalificacionLugarService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar.VwCalificacionLugarService;
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
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionLugarController {

    private final CalificacionLugarService calService; // tabla
    private final VwCalificacionLugarService vwService; // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/calificaciones/listar")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {}
    @GetMapping("/calificaciones/buscar/lugar/{q}")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> buscarPorLugar(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/calificaciones/buscar/cliente/{q}")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> buscarPorCliente(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCliente(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/calificaciones/buscar/comentario/{q}")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> buscarPorComentario(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorComentario(q, buildPageable(page, size, sort)));
    }

    // Exacta: /api/calificaciones/calificaciones/buscar/puntuacion/5
    @GetMapping("/calificaciones/buscar/puntuacion/{n}")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> buscarPorPuntuacionExacta(
            @PathVariable Integer n,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPuntuacionExacta(n, buildPageable(page, size, sort)));
    }

    // Rango: /api/calificaciones/calificaciones/buscar/puntuacion?min=3&max=5
    @GetMapping("/calificaciones/buscar/puntuacion")
    public ResponseEntity<Page<VwCalificacionLugarDTO>> buscarPorPuntuacionRango(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPuntuacionRango(min, max, buildPageable(page, size, sort)));
    }

    // Fecha: /api/calificaciones/calificaciones/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/calificaciones/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
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
    @PostMapping("/calificaciones")
    public ResponseEntity<?> crear(@Valid @RequestBody CalificacionLugarDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> field = new HashMap<>();
            result.getFieldErrors().forEach(err -> field.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", "Datos para insercion invalidos",
                    "errors", field
            ));
        }
        try {
            Long id = calService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "idCalificacionLugar", id
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
            log.error("Error al crear calificación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la calificación",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/calificaciones/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody CalificacionLugarDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            calService.actualizar(id, dto);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "CalificacionLugar no encontrada", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar calificación {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar calificación", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/calificaciones/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = calService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "CalificacionLugar no encontrada"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "CalificacionLugar eliminada correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar calificación {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar calificación", "detalle", e.getMessage()));
        }
    }
}
