package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EstadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.EstadoService;
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
@RequestMapping("/api/estados")
@RequiredArgsConstructor
public class EstadoController {

    private final EstadoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // GET: listar (paginado + orden) -> /api/estados/estados/listar
    @GetMapping("/estados/listar")
    public ResponseEntity<Page<EstadoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreEstado,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // GET: buscar por NOMBRE (parcial con {}) -> /api/estados/estados/buscar/{nombre}
    @GetMapping("/estados/buscar/{nombre}")
    public ResponseEntity<Page<EstadoDTO>> buscarPorNombre(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreEstado,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre, buildPageable(page, size, sort)));
    }

    // POST: crear -> /api/estados/estados
    @PostMapping("/estados")
    public ResponseEntity<?> crear(@Valid @RequestBody EstadoDTO dto, BindingResult result) {
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
            log.error("Error al crear estado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el estado",
                    "detail", e.getMessage()
            ));
        }
    }

    // PUT: actualizar por ID -> /api/estados/estados/{id}
    @PutMapping("/estados/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody EstadoDTO dto,
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
                    .body(Map.of("error", "Estado no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar estado {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar estado", "detalle", e.getMessage()));
        }
    }

    // DELETE: eliminar por ID -> /api/estados/estados/{id}
    @DeleteMapping("/estados/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminarPorId(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Estado no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Estado eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar estado {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar estado", "detalle", e.getMessage()));
        }
    }
}
