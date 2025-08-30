package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.DetallePagoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.DetallePagoService;
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
@RequestMapping("/api/detalle-pagos")
@RequiredArgsConstructor
public class DetallePagoController {

    private final DetallePagoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/detalle-pagos/detalles/listar
    @GetMapping("/detalles/listar")
    public ResponseEntity<Page<DetallePagoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetallePago,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // por descripción (parcial) -> /api/detalle-pagos/detalles/buscar/descripcion/{q}
    @GetMapping("/detalles/buscar/descripcion/{q}")
    public ResponseEntity<Page<DetallePagoDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetallePago,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // por pago exacto -> /api/detalle-pagos/detalles/buscar/pago/{idPago}
    @GetMapping("/detalles/buscar/pago/{idPago}")
    public ResponseEntity<Page<DetallePagoDTO>> buscarPorPago(
            @PathVariable Long idPago,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idDetallePago,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPago(idPago, buildPageable(page, size, sort)));
    }

    // por monto (rango) -> /api/detalle-pagos/detalles/buscar/monto?min=0&max=100
    @GetMapping("/detalles/buscar/monto")
    public ResponseEntity<Page<DetallePagoDTO>> buscarPorMonto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "monto,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorMonto(min, max, buildPageable(page, size, sort)));
    }

    // ===== POST crear -> /api/detalle-pagos/detalles
    @PostMapping("/detalles")
    public ResponseEntity<?> crear(@Valid @RequestBody DetallePagoDTO dto, BindingResult result) {
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
                    "idDetallePago", id
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
            log.error("Error al crear detalle de pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el detalle de pago",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/detalle-pagos/detalles/{id}
    @PutMapping("/detalles/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody DetallePagoDTO dto,
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
                    .body(Map.of("error", "DetallePago no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar detalle de pago {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar detalle de pago", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/detalle-pagos/detalles/{id}
    @DeleteMapping("/detalles/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "DetallePago no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Detalle de pago eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar detalle de pago {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar detalle de pago", "detalle", e.getMessage()));
        }
    }
}
