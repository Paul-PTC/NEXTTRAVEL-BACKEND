package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.GastoTipoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwGastoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.GastoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.VwGastoService;
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
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {
        "http://127.0.0.1:5501","http://localhost:5501",
        "http://127.0.0.1:5500","http://localhost:5500",
        "http://127.0.0.1:5502","http://localhost:5502"
})
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService service;     // tabla
    private final VwGastoService vwService; // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // listar solo tipo de gasto
    @GetMapping("/tipos")
    public ResponseEntity<List<GastoTipoDTO>> listarTipos() {
        return ResponseEntity.ok(service.listarTipos());
    }
    // ===== GET (vista) =====
    // Listar -> /api/gastos/gastos/listar
    @GetMapping("/gastos/listar")
    public ResponseEntity<Page<VwGastoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales {}
    // /api/gastos/gastos/buscar/tipo/{q}
    @GetMapping("/gastos/buscar/tipo/{q}")
    public ResponseEntity<Page<VwGastoDTO>> buscarPorTipo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorTipo(q, buildPageable(page, size, sort)));
    }

    // /api/gastos/gastos/buscar/descripcion/{q}
    @GetMapping("/gastos/buscar/descripcion/{q}")
    public ResponseEntity<Page<VwGastoDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // Filtros por monto y fecha
    // /api/gastos/gastos/buscar/monto?min=0&max=500
    @GetMapping("/gastos/buscar/monto")
    public ResponseEntity<Page<VwGastoDTO>> buscarPorMonto(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "monto,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorMonto(min, max, buildPageable(page, size, sort)));
    }

    // /api/gastos/gastos/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/gastos/buscar/fecha")
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

    // ===== POST/PUT/DELETE (tabla) =====
    // Crear -> /api/gastos/gastos
    @PostMapping("/gastosC")
    public ResponseEntity<?> crear(@Valid @RequestBody GastoDTO dto, BindingResult result) {
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
                    "idGasto", id
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
            log.error("Error al crear gasto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el gasto",
                    "detail", e.getMessage()
            ));
        }
    }

    // Actualizar -> /api/gastos/gastos/{id}
    @PutMapping("/gastosU/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody GastoDTO dto,
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
                    .body(Map.of("error", "Gasto no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar gasto {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar gasto", "detalle", e.getMessage()));
        }
    }

    // Eliminar -> /api/gastos/gastos/{id}
    @DeleteMapping("/gastosE/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Gasto no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Gasto eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar gasto {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar gasto", "detalle", e.getMessage()));
        }
    }
}
