package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GananciaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwGananciaDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.GananciaService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.VwGananciaService;
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
@CrossOrigin(origins = {"http://127.0.0.1:5502", "http://localhost:5502"})
@RestController
@RequestMapping("/api/ganancias")
@RequiredArgsConstructor
public class GananciaController {

    private final GananciaService service;      // tabla
    private final VwGananciaService vwService;  // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/ganancias/listar")
    public ResponseEntity<Page<VwGananciaDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }


    // Búsquedas parciales con {}
    @GetMapping("/ganancias/buscar/cliente/{q}")
    public ResponseEntity<Page<VwGananciaDTO>> buscarPorCliente(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCliente(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/ganancias/buscar/lugar/{q}")
    public ResponseEntity<Page<VwGananciaDTO>> buscarPorLugar(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(q, buildPageable(page, size, sort)));
    }

    // Filtros por montos (rango)
    @GetMapping("/ganancias/buscar/monto-bruto")
    public ResponseEntity<Page<VwGananciaDTO>> buscarPorMontoBruto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "montoBruto,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorMontoBruto(min, max, buildPageable(page, size, sort)));
    }

    @GetMapping("/ganancias/buscar/monto-neto")
    public ResponseEntity<Page<VwGananciaDTO>> buscarPorMontoNeto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "montoNeto,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorMontoNeto(min, max, buildPageable(page, size, sort)));
    }

    // Rango de fechas (ISO-8601)
    @GetMapping("/ganancias/buscar/fecha")
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
            return ResponseEntity.ok(vwService.buscarPorFecha(d, h, buildPageable(page, size, sort)));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "VALIDATION_ERROR",
                    "message", "Formato de fecha inválido. Usa ISO-8601: 2025-01-31T12:34:56"
            ));
        }
    }

    // Por reserva exacta
    @GetMapping("/ganancias/buscar/reserva/{idReserva}")
    public ResponseEntity<Page<VwGananciaDTO>> buscarPorReserva(
            @PathVariable Long idReserva,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorReserva(idReserva, buildPageable(page, size, sort)));
    }

    // ===== POST/PUT/DELETE (tabla) =====
    @PostMapping("/gananciasI")
    public ResponseEntity<?> crear(@Valid @RequestBody GananciaDTO dto, BindingResult result) {
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
                    "idGanancia", id
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
            log.error("Error al crear ganancia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear la ganancia",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/ganancias/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody GananciaDTO dto,
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
                    .body(Map.of("error", "Ganancia no encontrada", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar ganancia {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar ganancia", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/ganancias/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Ganancia no encontrada"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Ganancia eliminada correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar ganancia {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar ganancia", "detalle", e.getMessage()));
        }
    }
}
