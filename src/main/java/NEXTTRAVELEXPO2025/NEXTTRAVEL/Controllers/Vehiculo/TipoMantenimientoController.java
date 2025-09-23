package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.TipoMantenimientoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.TipoMantenimientoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tipo-mantenimientos")
@CrossOrigin(origins = {
        "http://127.0.0.1:5501","http://localhost:5501",
        "http://127.0.0.1:5500","http://localhost:5500",
        "http://127.0.0.1:5502","http://localhost:5502"
})
@RequiredArgsConstructor
public class TipoMantenimientoController {

    private final TipoMantenimientoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/tipo-mantenimientos/tipos/listar
    @GetMapping("/tipos/listar")
    public ResponseEntity<Page<TipoMantenimientoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreTipo,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // por nombre -> /api/tipo-mantenimientos/tipos/buscar/nombre/{q}
    @GetMapping("/tipos/buscar/nombre/{q}")
    public ResponseEntity<Page<TipoMantenimientoDTO>> buscarPorNombre(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreTipo,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorNombre(q, buildPageable(page, size, sort)));
    }

    // por descripción -> /api/tipo-mantenimientos/tipos/buscar/descripcion/{q}
    @GetMapping("/tipos/buscar/descripcion/{q}")
    public ResponseEntity<Page<TipoMantenimientoDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreTipo,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // ===== POST crear -> /api/tipo-mantenimientos/tipos
    @PostMapping("/tipos")
    public ResponseEntity<?> crear(@Valid @RequestBody TipoMantenimientoDTO dto, BindingResult result) {
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
                    "idTipoMantenimiento", id
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear TipoMantenimiento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el tipo de mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/tipo-mantenimientos/tipos/{id}
    @PutMapping("/tipos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody TipoMantenimientoDTO dto,
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
                    .body(Map.of("error", "TipoMantenimiento no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar TipoMantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar tipo de mantenimiento", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/tipo-mantenimientos/tipos/{id}
    @DeleteMapping("/tipos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "TipoMantenimiento no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Tipo de mantenimiento eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar TipoMantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar tipo de mantenimiento", "detalle", e.getMessage()));
        }
    }
}
