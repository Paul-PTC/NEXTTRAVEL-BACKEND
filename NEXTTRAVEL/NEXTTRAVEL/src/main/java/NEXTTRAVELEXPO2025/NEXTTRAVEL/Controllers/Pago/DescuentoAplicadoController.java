package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Pago;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.DescuentoAplicadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Pago.VwDescuentoAplicadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.DescuentoAplicadoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Pago.VwDescuentoAplicadoService;
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
@RequestMapping("/api/descuentos")
@RequiredArgsConstructor
public class DescuentoAplicadoController {

    private final DescuentoAplicadoService service; // tabla
    private final VwDescuentoAplicadoService vwService; // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/descuentos/listar")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {}
    @GetMapping("/descuentos/buscar/cliente/{q}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorCliente(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCliente(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/descuentos/buscar/lugar/{q}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorLugar(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorLugar(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/descuentos/buscar/tipo/{tipo}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorTipo(tipo, buildPageable(page, size, sort)));
    }

    @GetMapping("/descuentos/buscar/promocion/{q}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorPromocion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPromocion(q, buildPageable(page, size, sort)));
    }

    // % exacto o rango
    @GetMapping("/descuentos/buscar/porcentaje/{n}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorPorcentaje(
            @PathVariable Integer n,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPorcentaje(n, buildPageable(page, size, sort)));
    }

    @GetMapping("/descuentos/buscar/porcentaje")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorPorcentajeRango(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "porcentajeAplicado,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPorcentajeRango(min, max, buildPageable(page, size, sort)));
    }

    // por fecha e idReserva
    @GetMapping("/descuentos/buscar/fecha")
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

    @GetMapping("/descuentos/buscar/reserva/{idReserva}")
    public ResponseEntity<Page<VwDescuentoAplicadoDTO>> buscarPorReserva(
            @PathVariable Long idReserva,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorReserva(idReserva, buildPageable(page, size, sort)));
    }

    // ===== POST/PUT/DELETE (tabla) =====
    @PostMapping("/descuentos")
    public ResponseEntity<?> crear(@Valid @RequestBody DescuentoAplicadoDTO dto, BindingResult result) {
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
                    "idDescuentoAplicado", id
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
            log.error("Error al crear descuento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el descuento aplicado",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/descuentos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody DescuentoAplicadoDTO dto,
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
                    .body(Map.of("error", "DescuentoAplicado no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar descuento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar descuento", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/descuentos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "DescuentoAplicado no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Descuento aplicado eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar descuento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar descuento aplicado", "detalle", e.getMessage()));
        }
    }
}
