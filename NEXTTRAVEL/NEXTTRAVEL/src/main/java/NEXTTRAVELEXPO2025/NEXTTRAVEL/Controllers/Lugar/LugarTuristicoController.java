package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Lugar;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Lugar.LugarTuristicoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Lugar.LugarTuristicoService;
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
@RequestMapping("/api/lugares")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://127.0.0.1:5501","http://localhost:5501",
        "http://127.0.0.1:5500","http://localhost:5500",
        "http://127.0.0.1:5502","http://localhost:5502"
})
public class LugarTuristicoController {

    private final LugarTuristicoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // GET: listar (paginado + orden) -> /api/lugares/lugares/listar
    @GetMapping("/lugares/listar")
    public ResponseEntity<Page<LugarTuristicoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreLugar,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // /api/lugares/lugares/buscar/{nombre}
    @GetMapping("/lugares/buscar/{nombre}")
    public ResponseEntity<Page<LugarTuristicoDTO>> buscarPorNombre(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreLugar,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre, buildPageable(page, size, sort)));
    }

    // /api/lugares/lugares/buscar/ubicacion/{q}
    @GetMapping("/lugares/buscar/ubicacion/{q}")
    public ResponseEntity<Page<LugarTuristicoDTO>> buscarPorUbicacion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreLugar,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorUbicacion(q, buildPageable(page, size, sort)));
    }

    // /api/lugares/lugares/buscar/tipo/{q}
    @GetMapping("/lugares/buscar/tipo/{q}")
    public ResponseEntity<Page<LugarTuristicoDTO>> buscarPorTipo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreLugar,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorTipo(q, buildPageable(page, size, sort)));
    }

    // /api/lugares/lugares/buscar/descripcion/{q}
    @GetMapping("/lugares/buscar/descripcion/{q}")
    public ResponseEntity<Page<LugarTuristicoDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreLugar,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // POST: crear -> /api/lugares/lugares
    @PostMapping("/lugares")
    public ResponseEntity<?> crear(@Valid @RequestBody LugarTuristicoDTO dto, BindingResult result) {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", service.crear(dto)
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear lugar", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el lugar",
                    "detail", e.getMessage()
            ));
        }
    }

    // PUT: actualizar por ID -> /api/lugares/lugares/{id}
    @PutMapping("/lugares/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody LugarTuristicoDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            return ResponseEntity.ok(service.actualizarPorId(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Lugar no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar lugar {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar lugar", "detalle", e.getMessage()));
        }
    }

    // DELETE: eliminar por ID -> /api/lugares/lugares/{id}
    @DeleteMapping("/lugares/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminarPorId(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Lugar no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Lugar eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar lugar {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar lugar", "detalle", e.getMessage()));
        }
    }
}
