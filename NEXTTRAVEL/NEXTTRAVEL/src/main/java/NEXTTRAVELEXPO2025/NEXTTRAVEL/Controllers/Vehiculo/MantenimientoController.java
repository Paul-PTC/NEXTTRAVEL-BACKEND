package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.*;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.MantenimientoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.TipoMantenimientoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.VehiculoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.VwMantenimientoService;
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
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://127.0.0.1:5500","http://localhost:5500","http://127.0.0.1:5502","http://localhost:5502"})
@Slf4j
@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService service;     // tabla
    private final VwMantenimientoService vwService; // vista
    private final VehiculoService vehiculoService;
    private final TipoMantenimientoService tipoMantenimientoService;


    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    @GetMapping
    public ResponseEntity<List<ListarTipoMantenimientoDTO>> listarTipos() {
        return ResponseEntity.ok(service.listarTipos());
    }

    // GET /api/lookups/vehiculos -> [{ idVehiculo, placa }]
    @GetMapping("/vehiculos")
    public ResponseEntity<List<VehiculoPlacaDTO>> listarVehiculosMin() {
        return ResponseEntity.ok(vehiculoService.listarSoloPlacas());
    }

    // Tipos de mantenimiento (id + nombreTipo) → para combo de tipos
    @GetMapping("/tipos-mantenimiento")
    public ResponseEntity<List<TipoMantenimientoMinDTO>> listarTiposMin() {
        return ResponseEntity.ok(tipoMantenimientoService.listarSoloTipos()); // ✅ usa el service correcto
    }

    // ===== GET (vista) =====
    // Listar -> /api/mantenimientos/mantenimientos/listar
    @GetMapping("/mantenimientos/listar")
    public ResponseEntity<Page<VwMantenimientoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    // Búsquedas parciales {}
    @GetMapping("/mantenimientos/buscar/placa/{q}")
    public ResponseEntity<Page<VwMantenimientoDTO>> buscarPorPlaca(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorPlaca(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/mantenimientos/buscar/modelo/{q}")
    public ResponseEntity<Page<VwMantenimientoDTO>> buscarPorModelo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorModelo(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/mantenimientos/buscar/tipo/{q}")
    public ResponseEntity<Page<VwMantenimientoDTO>> buscarPorTipo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorTipo(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/mantenimientos/buscar/descripcion/{q}")
    public ResponseEntity<Page<VwMantenimientoDTO>> buscarPorDescripcion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorDescripcion(q, buildPageable(page, size, sort)));
    }

    // Rango de fechas (ISO-8601) -> /api/mantenimientos/mantenimientos/buscar/fecha?desde=...&hasta=...
    @GetMapping("/mantenimientos/buscar/fecha")
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
    // Crear -> /api/mantenimientos/mantenimientos
    @PostMapping("/mantenimientos")
    public ResponseEntity<?> crear(@Valid @RequestBody MantenimientoDTO dto, BindingResult result) {
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
                    "idMantenimiento", id
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
            log.error("Error al crear mantenimiento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el mantenimiento",
                    "detail", e.getMessage()
            ));
        }
    }

    // Actualizar -> /api/mantenimientos/mantenimientos/{id}
    @PutMapping("/mantenimientosU/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody MantenimientoDTO dto,
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
                    .body(Map.of("error", "Mantenimiento no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar mantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar mantenimiento", "detalle", e.getMessage()));
        }
    }

    // Eliminar -> /api/mantenimientos/mantenimientos/{id}
    @DeleteMapping("/mantenimientos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Mantenimiento no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Mantenimiento eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar mantenimiento {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar mantenimiento", "detalle", e.getMessage()));
        }
    }
}
