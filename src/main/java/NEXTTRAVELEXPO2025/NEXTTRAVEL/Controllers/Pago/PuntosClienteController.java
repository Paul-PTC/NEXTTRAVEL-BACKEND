package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PuntosClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.PuntosClienteService;
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
@RequestMapping("/api/puntos")
@RequiredArgsConstructor
public class PuntosClienteController {

    private final PuntosClienteService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET: listar (vista no aplica, usamos la tabla) -> /api/puntos/puntos/listar
    @GetMapping("/puntos/listar")
    public ResponseEntity<Page<PuntosClienteDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaActualizacion,desc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // /api/puntos/puntos/buscar/{duiLike}
    @GetMapping("/puntos/buscar/{duiLike}")
    public ResponseEntity<Page<PuntosClienteDTO>> buscarPorDui(
            @PathVariable String duiLike,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaActualizacion,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDui(duiLike, buildPageable(page, size, sort)));
    }

    // /api/puntos/puntos/buscar/puntos?min=100&max=500
    @GetMapping("/puntos/buscar/puntos")
    public ResponseEntity<Page<PuntosClienteDTO>> buscarPorPuntos(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntos,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPuntos(min, max, buildPageable(page, size, sort)));
    }

    // /api/puntos/puntos/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/puntos/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaActualizacion,desc") String sort
    ) {
        try {
            LocalDateTime d = LocalDateTime.parse(desde);
            LocalDateTime h = LocalDateTime.parse(hasta);
            return ResponseEntity.ok(service.buscarPorFecha(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato de fecha inválido. Usa ISO-8601: 2025-01-31T12:34:56"
            ));
        }
    }

    // ===== POST: crear -> /api/puntos/puntos
    @PostMapping("/puntos")
    public ResponseEntity<?> crear(@Valid @RequestBody PuntosClienteDTO dto, BindingResult result) {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", service.crear(dto)
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
            log.error("Error al crear puntos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear puntos del cliente",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT: actualizar por DUI -> /api/puntos/puntos/{dui}
    @PutMapping("/puntos/{dui}")
    public ResponseEntity<?> actualizar(@PathVariable String dui,
                                        @Valid @RequestBody PuntosClienteDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            return ResponseEntity.ok(service.actualizar(dui, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Puntos del cliente no encontrados", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar puntos {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar puntos", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE: eliminar por DUI -> /api/puntos/puntos/{dui}
    @DeleteMapping("/puntos/{dui}")
    public ResponseEntity<?> eliminar(@PathVariable String dui) {
        try {
            boolean eliminado = service.eliminar(dui);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Puntos del cliente no encontrados"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Puntos del cliente eliminados correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar puntos {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar puntos", "detalle", e.getMessage()));
        }
    }
}
