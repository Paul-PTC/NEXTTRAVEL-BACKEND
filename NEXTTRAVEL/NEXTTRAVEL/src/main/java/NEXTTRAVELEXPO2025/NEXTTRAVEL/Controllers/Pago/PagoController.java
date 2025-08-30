package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.PagoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.PagoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/pagos/pagos/listar
    @GetMapping("/pagos/listar")
    public ResponseEntity<Page<PagoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    // por método (parcial) -> /api/pagos/pagos/buscar/metodo/{q}
    @GetMapping("/pagos/buscar/metodo/{q}")
    public ResponseEntity<Page<PagoDTO>> buscarPorMetodo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorMetodo(q, buildPageable(page, size, sort)));
    }

    // por reserva exacta -> /api/pagos/pagos/buscar/reserva/{idReserva}
    @GetMapping("/pagos/buscar/reserva/{idReserva}")
    public ResponseEntity<Page<PagoDTO>> buscarPorReserva(
            @PathVariable Long idReserva,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorReserva(idReserva, buildPageable(page, size, sort)));
    }

    // monto (rango) -> /api/pagos/pagos/buscar/monto?min=0&max=500
    @GetMapping("/pagos/buscar/monto")
    public ResponseEntity<Page<PagoDTO>> buscarPorMonto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "monto,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorMonto(min, max, buildPageable(page, size, sort)));
    }

    // fecha (rango ISO-8601) -> /api/pagos/pagos/buscar/fecha?desde=...&hasta=...
    @GetMapping("/pagos/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
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

    // ===== POST crear -> /api/pagos/pagos
    @PostMapping("/pagos")
    public ResponseEntity<?> crear(@Valid @RequestBody PagoDTO dto, BindingResult result) {
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
                    "idPago", id
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
            log.error("Error al crear pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el pago",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/pagos/pagos/{id}
    @PutMapping("/pagos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody PagoDTO dto,
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
                    .body(Map.of("error", "Pago no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar pago {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar pago", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/pagos/pagos/{id}
    @DeleteMapping("/pagos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pago no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Pago eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar pago {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar pago", "detalle", e.getMessage()));
        }
    }
}
