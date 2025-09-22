package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Vehiculo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Vehiculo.VehiculoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Vehiculo.VehiculoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService service;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET listar -> /api/vehiculos/vehiculos/listar
    @GetMapping("/vehiculos/listar")
    public ResponseEntity<Page<VehiculoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa,asc") String sort
    ) {
        return ResponseEntity.ok(service.listar(buildPageable(page, size, sort)));
    }

    // ===== BÚSQUEDAS parciales con {} =====
    @GetMapping("/vehiculos/buscar/placa/{q}")
    public ResponseEntity<Page<VehiculoDTO>> buscarPorPlaca(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorPlaca(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/vehiculos/buscar/modelo/{q}")
    public ResponseEntity<Page<VehiculoDTO>> buscarPorModelo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "modelo,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorModelo(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/vehiculos/buscar/estado/{q}")
    public ResponseEntity<Page<VehiculoDTO>> buscarPorEstado(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "estado,asc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorEstado(q, buildPageable(page, size, sort)));
    }

    // RANGOS
    @GetMapping("/vehiculos/buscar/capacidad")
    public ResponseEntity<Page<VehiculoDTO>> buscarPorCapacidad(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "capacidad,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorCapacidad(min, max, buildPageable(page, size, sort)));
    }

    @GetMapping("/vehiculos/buscar/anio")
    public ResponseEntity<Page<VehiculoDTO>> buscarPorAnio(
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "anioFabricacion,desc") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorAnio(min, max, buildPageable(page, size, sort)));
    }

    // ===== POST crear -> /api/vehiculos/vehiculos
    @PostMapping("/vehiculosC")
    public ResponseEntity<?> crear(@Valid @RequestBody VehiculoDTO dto, BindingResult result) {
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
                    "idVehiculo", id
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear vehiculo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el vehículo",
                    "detail", e.getMessage()
            ));
        }
    }

    // ===== PUT actualizar -> /api/vehiculos/vehiculos/{id}
    @PutMapping("/vehiculosA/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody VehiculoDTO dto,
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
                    .body(Map.of("error", "Vehiculo no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar vehiculo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar vehículo", "detalle", e.getMessage()));
        }
    }

    // ===== DELETE eliminar -> /api/vehiculos/vehiculos/{id}
    @DeleteMapping("/vehiculosE/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Vehiculo no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Vehículo eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar vehiculo {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar vehículo", "detalle", e.getMessage()));
        }
    }
}
