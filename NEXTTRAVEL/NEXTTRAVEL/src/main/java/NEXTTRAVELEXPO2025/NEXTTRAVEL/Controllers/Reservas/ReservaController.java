package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.ReservaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.VwReservaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas.ReservaService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas.VwReservaService;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService; // tabla
    private final VwReservaService vwService;    // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/reservas/listar")
    public ResponseEntity<Page<VwReservaDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {}
    @GetMapping("/reservas/buscar/cliente/{q}")
    public ResponseEntity<Page<VwReservaDTO>> buscarPorNombreCliente(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorNombreCliente(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/reservas/buscar/dui/{q}")
    public ResponseEntity<Page<VwReservaDTO>> buscarPorDui(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorDui(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/reservas/buscar/lugar/{q}")
    public ResponseEntity<Page<VwReservaDTO>> buscarPorLugar(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/reservas/buscar/estado/{q}")
    public ResponseEntity<Page<VwReservaDTO>> buscarPorEstado(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorEstado(q, buildPageable(page, size, sort)));
    }

    // Rango de cantidad: /api/reservas/reservas/buscar/cantidad?min=1&max=10
    @GetMapping("/reservas/buscar/cantidad")
    public ResponseEntity<Page<VwReservaDTO>> buscarPorCantidad(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCantidad(min, max, buildPageable(page, size, sort)));
    }

    // Rango de fechas ISO: /api/reservas/reservas/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/reservas/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaReserva,desc") String sort
    ) {
        try {
            LocalDateTime d = LocalDateTime.parse(desde);
            LocalDateTime h = LocalDateTime.parse(hasta);
            return ResponseEntity.ok(vwService.buscarPorFecha(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato de fecha inválido. Usa ISO-8601: 2025-01-31T12:34:56"
            ));
        }
    }

    // ===== POST/PUT/DELETE (tabla) =====
    @PostMapping("/reservasC")
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaDTO dto, BindingResult result) {
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
            Long id = reservaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "idReserva", id
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "errorType", "NOT_FOUND",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear reserva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la reserva",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/reservasA/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ReservaDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            reservaService.actualizar(id, dto);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reserva no encontrada", "mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar reserva {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar reserva", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/reservasD/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = reservaService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Reserva no encontrada"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Reserva eliminada correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar reserva {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar reserva", "detalle", e.getMessage()));
        }
    }
}
