package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.EmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.VwEmpleadoDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.EmpleadoService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.VwEmpleadoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empService;
    private final VwEmpleadoService vwService;

    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // ===== GET (vista) =====
    @GetMapping("/EmpleadoListar")
    public ResponseEntity<Page<VwEmpleadoDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.listar(buildPageable(page, size, sort)));
    }

    @GetMapping("/buscar/{q}")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorNombre(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorNombre(q, buildPageable(page, size, sort)));
    }


    @GetMapping("/buscar/correo/{q}")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorCorreo(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorCorreo(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/buscar/telefono/{q}")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorTelefono(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorTelefono(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/buscar/direccion/{q}")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorDireccion(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorDireccion(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/buscar/rango/{q}")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorRango(
            @PathVariable String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaContratacion,desc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorRango(q, buildPageable(page, size, sort)));
    }

    @GetMapping("/buscar/salario")
    public ResponseEntity<Page<VwEmpleadoDTO>> buscarPorSalario(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "salarioBase,asc") String sort
    ) {
        return ResponseEntity.ok(vwService.buscarPorSalario(min, max, buildPageable(page, size, sort)));
    }

    // ===== POST/PUT/DELETE (tabla) =====
    @PostMapping("/EmpleadoC")
    public ResponseEntity<?> crear(@Valid @RequestBody EmpleadoDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            result.getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", "Datos para insercion invalidos",
                    "errors", fieldErrors
            ));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", empService.crear(dto)
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "errorType", "NOT_FOUND",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear empleado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el empleado",
                    "detail", e.getMessage()
            ));
        }
    }

    @PutMapping("/EmpleadoA/{dui}")
    public ResponseEntity<?> actualizar(@PathVariable String dui,
                                        @Valid @RequestBody EmpleadoDTO dto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            return ResponseEntity.ok(empService.actualizarPorDui(dui, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Empleado no encontrado", "mensaje", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar empleado {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar empleado", "detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/EmpleadoE/{dui}")
    public ResponseEntity<?> eliminar(@PathVariable String dui) {
        try {
            boolean eliminado = empService.eliminarPorDui(dui);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Empleado no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Empleado eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar empleado {}: {}", dui, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar empleado", "detalle", e.getMessage()));
        }
    }
}