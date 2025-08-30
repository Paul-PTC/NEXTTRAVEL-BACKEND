package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PromocionDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.PromocionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/promociones/promociones/listar
    @GetMapping("/promociones/listar")
    public ResponseEntity<Page<PromocionDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaInicio,desc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== Búsquedas parciales {} =====
    @GetMapping("/promociones/buscar/nombre/{q}")
    public ResponseEntity<Page<PromocionDTO>> buscarPorNombre(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorNombre(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/promociones/buscar/descripcion/{q}")
    public ResponseEntity<Page<PromocionDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // ===== Descuento exacto / rango =====
    @GetMapping("/promociones/buscar/descuento/{n}")
    public ResponseEntity<Page<PromocionDTO>> buscarPorDescuento(
            @PathVariable Integer n,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descuentoPorcentaje,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescuento(n, buildPageable(page, size, sort)));
    }

    @GetMapping("/promociones/buscar/descuento")
    public ResponseEntity<Page<PromocionDTO>> buscarPorDescuentoRango(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descuentoPorcentaje,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescuentoRango(min, max, buildPageable(page, size, sort)));
    }

    // ===== Vigentes en fecha (por defecto hoy) =====
    @GetMapping("/promociones/buscar/vigentes")
    public ResponseEntity<?> vigentes(
            @RequestParam(required = false) String fecha, // ISO yyyy-MM-dd
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaInicio,desc") String sort
    ) {
        try {
            LocalDate d = (fecha == null || fecha.isBlank()) ? LocalDate.now() : LocalDate.parse(fecha);
            return ResponseEntity.ok(service.buscarVigentes(d, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato de fecha inválido. Usa ISO: 2025-08-29"
            ));
        }
    }

    // ===== Overlap con rango de fechas =====
    @GetMapping("/promociones/buscar/fechas")
    public ResponseEntity<?> buscarPorRango(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaInicio,desc") String sort
    ) {
        try {
            LocalDate d = (desde == null || desde.isBlank()) ? null : LocalDate.parse(desde);
            LocalDate h = (hasta == null || hasta.isBlank()) ? null : LocalDate.parse(hasta);
            return ResponseEntity.ok(service.buscarPorRango(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Fechas inválidas. Usa ISO: 2025-08-29"
            ));
        }
    }

    // ===== POST crear -> /api/promociones/promociones
    @PostMapping("/promociones")
    public ResponseEntity<?> crear(@Valid @RequestBody PromocionDTO dto, BindingResult result) {
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
                    "idPromocion", id
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear promoción", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la promoción",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/promociones/promociones/{id}
    @PutMapping("/promociones/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody PromocionDTO dto,
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
                    .body(Map.of("error", "Promoción no encontrada", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar promoción {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar promoción", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/promociones/promociones/{id}
    @DeleteMapping("/promociones/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Promoción no encontrada"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Promoción eliminada correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar promoción {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar promoción", "detalle", e.getMessage()));
        }
    }
}
