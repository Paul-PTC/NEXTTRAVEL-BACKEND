package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.RangoEmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.RangoEmpleadoService;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/rangos")
@RequiredArgsConstructor
public class RangoEmpleadoController {

    private final RangoEmpleadoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // GET: listar (paginado + orden) -> /api/rangos/rangos/listar
    @GetMapping("/rangos/listar")
    public ResponseEntity<Page<RangoEmpleadoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreRango,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(service.listar(pageable));
    }

    // GET: buscar por NOMBRE (parcial) -> /api/rangos/rangos/buscar/{nombre}
    @GetMapping("/rangos/buscar/{nombre}")
    public ResponseEntity<Page<RangoEmpleadoDTO>> buscarPorNombre(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreRango,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(service.buscarPorNombre(nombre, pageable));
    }

    // GET: buscar por rango SALARIAL -> /api/rangos/rangos/buscar/salario?min=500.00&max=1200.00
    @GetMapping("/rangos/buscar/salario")
    public ResponseEntity<Page<RangoEmpleadoDTO>> buscarPorSalario(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "salarioBase,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(service.buscarPorSalario(min, max, pageable));
    }

    // POST: crear -> /api/rangos/rangos
    @PostMapping("/rangos")
    public ResponseEntity<?> crear(@Valid @RequestBody RangoEmpleadoDTO dto, BindingResult result) {
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
            log.error("Error al crear rango", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el rango",
                    "detail", e.getMessage()
            ));
        }
    }

    // PUT: actualizar por ID -> /api/rangos/rangos/{id}
    @PutMapping("/rangos/{id}")
    public ResponseEntity<?> actualizarPorId(
            @PathVariable Long id,
            @Valid @RequestBody RangoEmpleadoDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            result.getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        try {
            return ResponseEntity.ok(service.actualizarPorId(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rango no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar rango {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar rango", "detalle", e.getMessage()));
        }
    }

    // DELETE: eliminar por ID -> /api/rangos/rangos/{id}
    @DeleteMapping("/rangos/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminarPorId(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Rango no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Rango eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar rango {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar rango", "detalle", e.getMessage()));
        }
    }
}
