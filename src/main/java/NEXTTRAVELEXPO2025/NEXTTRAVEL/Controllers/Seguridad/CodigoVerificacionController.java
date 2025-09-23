package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Seguridad;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Seguridad.CodigoVerificacionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Seguridad.CodigoVerificacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/codigos")
@RequiredArgsConstructor
public class CodigoVerificacionController {

    private final CodigoVerificacionService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar (vista lógica sobre la tabla) -> /api/codigos/codigos/listar
    @GetMapping("/codigos/listar")
    public ResponseEntity<Page<CodigoVerificacionDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaGeneracion,desc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // por código (parcial) -> /api/codigos/codigos/buscar/codigo/{q}
    @GetMapping("/codigos/buscar/codigo/{q}")
    public ResponseEntity<Page<CodigoVerificacionDTO>> buscarPorCodigo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaGeneracion,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorCodigo(q, buildPageable(page, size, sort)));
    }

    // por usuario exacto -> /api/codigos/codigos/buscar/usuario/{idUsuario}
    @GetMapping("/codigos/buscar/usuario/{idUsuario}")
    public ResponseEntity<Page<CodigoVerificacionDTO>> buscarPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaGeneracion,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorUsuario(idUsuario, buildPageable(page, size, sort)));
    }

    // rango de generación (ISO-8601) -> /api/codigos/codigos/buscar/generacion?desde=...&hasta=...
    @GetMapping("/codigos/buscar/generacion")
    public ResponseEntity<?> buscarPorGeneracion(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaGeneracion,desc") String sort
    ) {
        try {
            LocalDateTime d = LocalDateTime.parse(desde);
            LocalDateTime h = LocalDateTime.parse(hasta);
            return ResponseEntity.ok(service.buscarPorGeneracion(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato inválido. Usa ISO-8601 para fechaGeneracion: 2025-01-31T12:34:56"
            ));
        }
    }

    // rango de validez (DATE) -> /api/codigos/codigos/buscar/validez?desde=YYYY-MM-DD&hasta=YYYY-MM-DD
    @GetMapping("/codigos/buscar/validez")
    public ResponseEntity<?> buscarPorValidez(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "validoHasta,asc") String sort
    ) {
        try {
            LocalDate d = LocalDate.parse(desde);
            LocalDate h = LocalDate.parse(hasta);
            return ResponseEntity.ok(service.buscarPorValidez(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato inválido. Usa ISO: 2025-01-31"
            ));
        }
    }

    // vigentes a una fecha -> /api/codigos/codigos/buscar/vigentes?fecha=YYYY-MM-DD
    @GetMapping("/codigos/buscar/vigentes")
    public ResponseEntity<?> buscarVigentes(
            @RequestParam String fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "validoHasta,asc") String sort
    ) {
        try {
            LocalDate f = LocalDate.parse(fecha);
            return ResponseEntity.ok(service.buscarVigentes(f, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato inválido. Usa ISO: 2025-01-31"
            ));
        }
    }

    // ===== POST crear -> /api/codigos/codigos
    @PostMapping("/codigos")
    public ResponseEntity<?> crear(@Valid @RequestBody CodigoVerificacionDTO dto, BindingResult result) {
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
            Long id = service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "idCodigo", id
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
            log.error("Error al crear codigo de verificacion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el código de verificación",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/codigos/codigos/{id}
    @PutMapping("/codigos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody CodigoVerificacionDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "CodigoVerificacion no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar codigo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar código", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/codigos/codigos/{id}
    @DeleteMapping("/codigos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "CodigoVerificacion no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Código de verificación eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar codigo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar código", "detalle", e.getMessage()));
        }
    }
}
