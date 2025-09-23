package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.DetalleMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.DetalleMantenimientoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/detalle-mantenimientos")
@RequiredArgsConstructor
public class DetalleMantenimientoController {

    private final DetalleMantenimientoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/detalle-mantenimientos/detalles/listar
    @GetMapping("/detalles/listar")
    public ResponseEntity<Page<DetalleMantenimientoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetalleMantenimiento,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // por actividad -> /api/detalle-mantenimientos/detalles/buscar/actividad/{q}
    @GetMapping("/detalles/buscar/actividad/{q}")
    public ResponseEntity<Page<DetalleMantenimientoDTO>> buscarPorActividad(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetalleMantenimiento,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorActividad(q, buildPageable(page, size, sort)));
    }

    // por mantenimiento exacto -> /api/detalle-mantenimientos/detalles/buscar/mantenimiento/{idMantenimiento}
    @GetMapping("/detalles/buscar/mantenimiento/{idMantenimiento}")
    public ResponseEntity<Page<DetalleMantenimientoDTO>> buscarPorMantenimiento(
            @PathVariable Long idMantenimiento,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetalleMantenimiento,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorMantenimiento(idMantenimiento, buildPageable(page, size, sort)));
    }

    // por costo (rango) -> /api/detalle-mantenimientos/detalles/buscar/costo?min=0&max=100
    @GetMapping("/detalles/buscar/costo")
    public ResponseEntity<Page<DetalleMantenimientoDTO>> buscarPorCosto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "costo,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorCosto(min, max, buildPageable(page, size, sort)));
    }

    // ===== POST crear -> /api/detalle-mantenimientos/detalles
    @PostMapping("/detalles")
    public ResponseEntity<?> crear(@Valid @RequestBody DetalleMantenimientoDTO dto, BindingResult result) {
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
                    "idDetalleMantenimiento", id
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
            log.error("Error al crear detalle de mantenimiento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el detalle de mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/detalle-mantenimientos/detalles/{id}
    @PutMapping("/detalles/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody DetalleMantenimientoDTO dto,
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
                    .body(Map.of("error", "DetalleMantenimiento no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar detalle de mantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar detalle de mantenimiento", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/detalle-mantenimientos/detalles/{id}
    @DeleteMapping("/detalles/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "DetalleMantenimiento no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Detalle de mantenimiento eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar detalle de mantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar detalle de mantenimiento", "detalle", e.getMessage()));
        }
    }
}
