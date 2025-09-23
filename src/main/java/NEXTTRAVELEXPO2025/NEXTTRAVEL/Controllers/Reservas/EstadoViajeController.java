package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Reservas;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.EstadoViajeDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Reservas.VwEstadoViajeDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas.EstadoViajeService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Reservas.VwEstadoViajeService;
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
@RequestMapping("/api/estadoviaje")
@RequiredArgsConstructor
public class EstadoViajeController {

    private final EstadoViajeService service;   // tabla
    private final VwEstadoViajeService vwService; // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/estadoviaje/listar")
    public ResponseEntity<Page<VwEstadoViajeDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {}
    @GetMapping("/estadoviaje/buscar/cliente/{q}")
    public ResponseEntity<Page<VwEstadoViajeDTO>> buscarPorCliente(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCliente(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/estadoviaje/buscar/lugar/{q}")
    public ResponseEntity<Page<VwEstadoViajeDTO>> buscarPorLugar(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/estadoviaje/buscar/estado/{q}")
    public ResponseEntity<Page<VwEstadoViajeDTO>> buscarPorEstado(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorEstado(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/estadoviaje/buscar/reserva/{idReserva}")
    public ResponseEntity<Page<VwEstadoViajeDTO>> buscarPorReserva(
            @PathVariable Long idReserva, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorReserva(idReserva, buildPageable(page, size, sort)));
    }

    // Rango de fechas: /api/estadoviaje/estadoviaje/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/estadoviaje/buscar/fecha")
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
    @PostMapping("/estadoviaje")
    public ResponseEntity<?> crear(@Valid @RequestBody EstadoViajeDTO dto, BindingResult result) {
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
                    "idEstadoViaje", id
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "errorType", "NOT_FOUND",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear EstadoViaje", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el estado de viaje",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/estadoviaje/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody EstadoViajeDTO dto,
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
                    .body(Map.of("error", "EstadoViaje no encontrado", "mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar EstadoViaje {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar EstadoViaje", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/estadoviaje/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "EstadoViaje no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "EstadoViaje eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar EstadoViaje {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar EstadoViaje", "detalle", e.getMessage()));
        }
    }
}
