package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.MovimientoPuntosDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.MovimientoPuntosService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoPuntosController {

    private final MovimientoPuntosService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/movimientos/movimientos/listar
    @GetMapping("/movimientos/listar")
    public ResponseEntity<Page<MovimientoPuntosDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    @GetMapping("/movimientos/buscar/dui/{q}")
    public ResponseEntity<Page<MovimientoPuntosDTO>> buscarPorDui(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDui(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/movimientos/buscar/tipo/{tipo}")
    public ResponseEntity<Page<MovimientoPuntosDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo, buildPageable(page, size, sort)));
    }

    @GetMapping("/movimientos/buscar/descripcion/{q}")
    public ResponseEntity<Page<MovimientoPuntosDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/movimientos/buscar/reserva/{idReserva}")
    public ResponseEntity<Page<MovimientoPuntosDTO>> buscarPorReserva(
            @PathVariable Long idReserva,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorReserva(idReserva, buildPageable(page, size, sort)));
    }

    // rango puntos: ?min=1&max=500
    @GetMapping("/movimientos/buscar/puntos")
    public ResponseEntity<Page<MovimientoPuntosDTO>> buscarPorPuntos(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntosCambiados,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPuntos(min, max, buildPageable(page, size, sort)));
    }

    // rango fecha ISO
    @GetMapping("/movimientos/buscar/fecha")
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

    // ===== POST crear -> /api/movimientos/movimientos
    @PostMapping("/movimientos")
    public ResponseEntity<?> crear(@Valid @RequestBody MovimientoPuntosDTO dto, BindingResult result) {
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
                    "idMovimiento", id
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
            log.error("Error al crear movimiento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el movimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/movimientos/movimientos/{id}
    @PutMapping("/movimientos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody MovimientoPuntosDTO dto,
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
                    .body(Map.of("error", "Movimiento no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar movimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar movimiento", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar (revierte) -> /api/movimientos/movimientos/{id}
    @DeleteMapping("/movimientos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Movimiento no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Movimiento eliminado correctamente (saldo ajustado)"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al eliminar movimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar movimiento", "detalle", e.getMessage()));
        }
    }
}
