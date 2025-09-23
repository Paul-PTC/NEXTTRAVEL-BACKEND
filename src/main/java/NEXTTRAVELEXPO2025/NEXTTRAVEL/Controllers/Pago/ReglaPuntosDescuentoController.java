package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.ReglaPuntosDescuentoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.ReglaPuntosDescuentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reglas-puntos")
@RequiredArgsConstructor
public class ReglaPuntosDescuentoController {

    private final ReglaPuntosDescuentoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/reglas-puntos/reglas/listar
    @GetMapping("/reglas/listar")
    public ResponseEntity<Page<ReglaPuntosDescuentoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntosRequeridos,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS =====
    // puntos exactos -> /api/reglas-puntos/reglas/buscar/puntos/{n}
    @GetMapping("/reglas/buscar/puntos/{n}")
    public ResponseEntity<Page<ReglaPuntosDescuentoDTO>> buscarPorPuntosExacto(
            @PathVariable Integer n,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntosRequeridos,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPuntosExacto(n, buildPageable(page, size, sort)));
    }

    // rango puntos -> /api/reglas-puntos/reglas/buscar/puntos?min=100&max=1000
    @GetMapping("/reglas/buscar/puntos")
    public ResponseEntity<Page<ReglaPuntosDescuentoDTO>> buscarPorPuntosRango(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntosRequeridos,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPuntosRango(min, max, buildPageable(page, size, sort)));
    }

    // descuento exacto -> /api/reglas-puntos/reglas/buscar/descuento/{n}
    @GetMapping("/reglas/buscar/descuento/{n}")
    public ResponseEntity<Page<ReglaPuntosDescuentoDTO>> buscarPorDescuentoExacto(
            @PathVariable Integer n,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "porcentajeDescuento,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescuentoExacto(n, buildPageable(page, size, sort)));
    }

    // rango descuento -> /api/reglas-puntos/reglas/buscar/descuento?min=10&max=50
    @GetMapping("/reglas/buscar/descuento")
    public ResponseEntity<Page<ReglaPuntosDescuentoDTO>> buscarPorDescuentoRango(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "porcentajeDescuento,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescuentoRango(min, max, buildPageable(page, size, sort)));
    }

    // ===== POST crear -> /api/reglas-puntos/reglas
    @PostMapping("/reglas")
    public ResponseEntity<?> crear(@Valid @RequestBody ReglaPuntosDescuentoDTO dto, BindingResult result) {
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
                    "idRegla", id
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear regla", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la regla de puntos/descuento",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/reglas-puntos/reglas/{id}
    @PutMapping("/reglas/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ReglaPuntosDescuentoDTO dto,
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
                    .body(Map.of("error", "Regla no encontrada", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar regla {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar regla", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/reglas-puntos/reglas/{id}
    @DeleteMapping("/reglas/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Regla no encontrada"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Regla eliminada correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar regla {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar regla", "detalle", e.getMessage()));
        }
    }
}
