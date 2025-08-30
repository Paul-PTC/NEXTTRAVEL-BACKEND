package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.ClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.VwClienteDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.ClienteService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.VwClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;   // tabla
    private final VwClienteService vwService;      // vista

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/clientes/listar")
    public ResponseEntity<Page<VwClienteDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales con {} (vista)
    @GetMapping("/clientes/buscar/{q}")
    public ResponseEntity<Page<VwClienteDTO>> buscarPorNombre(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorNombre(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/clientes/buscar/correo/{q}")
    public ResponseEntity<Page<VwClienteDTO>> buscarPorCorreo(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCorreo(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/clientes/buscar/telefono/{q}")
    public ResponseEntity<Page<VwClienteDTO>> buscarPorTelefono(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorTelefono(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/clientes/buscar/direccion/{q}")
    public ResponseEntity<Page<VwClienteDTO>> buscarPorDireccion(
            @PathVariable String q, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorDireccion(q, buildPageable(page, size, sort)));
    }

    // Rango de puntos: /api/clientes/clientes/buscar/puntos?min=100&max=500
    @GetMapping("/clientes/buscar/puntos")
    public ResponseEntity<Page<VwClienteDTO>> buscarPorPuntos(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "puntosActuales,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPuntos(min, max, buildPageable(page, size, sort)));
    }

    // Rango de fechas (ISO-8601): /api/clientes/clientes/buscar/fecha?desde=2025-01-01T00:00:00&hasta=2025-12-31T23:59:59
    @GetMapping("/clientes/buscar/fecha")
    public ResponseEntity<?> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort
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
    @PostMapping("/clientes")
    public ResponseEntity<?> crear(@Valid @RequestBody ClienteDTO dto, BindingResult result) {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", clienteService.crear(dto)
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "errorType", "NOT_FOUND",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el cliente",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/clientes/{dui}")
    public ResponseEntity<?> actualizar(@PathVariable String dui,
                                        @Valid @RequestBody ClienteDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            return ResponseEntity.ok(clienteService.actualizarPorDui(dui, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cliente no encontrado", "mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar cliente {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar cliente", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/clientes/{dui}")
    public ResponseEntity<?> eliminar(@PathVariable String dui) {
        try {
            boolean eliminado = clienteService.eliminarPorDui(dui);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Cliente no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar cliente {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar cliente", "detalle", e.getMessage()));
        }
    }
}