package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.UsuarioDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ===== Helpers =====
    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // GET: listar todos (paginado + orden) -> /api/usuarios/usuarios/listar
    @GetMapping("/usuarios/listar")
    public ResponseEntity<Page<UsuarioDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreUsuario,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    // GET: buscar por NOMBRE (parcial) -> /api/usuarios/usuarios/buscar/{nombre}
    @GetMapping("/usuarios/buscarN/{nombre}")
    public ResponseEntity<Page<UsuarioDTO>> buscarPorNombre(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreUsuario,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre, pageable));
    }

    // GET: buscar por CORREO (parcial; recuerda URL-encode para '@') -> /api/usuarios/usuarios/buscar/correo/{correo}
    @GetMapping("/usuarios/buscarC/{correo}")
    public ResponseEntity<Page<UsuarioDTO>> buscarPorCorreo(
            @PathVariable String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "correo,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo, pageable));
    }

    // GET: buscar por ROL (exacto) -> /api/usuarios/usuarios/buscar/rol/{rol}
    @GetMapping("/usuarios/buscarR/{rol}")
    public ResponseEntity<Page<UsuarioDTO>> buscarPorRol(
            @PathVariable String rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rol,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol, pageable));
    }

    // POST: crear -> /api/usuarios/usuarios
    @PostMapping("/usuarios")
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioDTO dto, BindingResult result) {
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
                    "data", usuarioService.crear(dto)
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Insercion Incorrecta",
                    "errorType", "VALIDATION_ERROR",
                    "message", ex.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Error al crear el usuario",
                    "detail", e.getMessage()
            ));
        }
    }

    // PUT: actualizar por ID -> /api/usuarios/usuarios/{id}
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarPorId(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO dto,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            result.getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        try {
            return ResponseEntity.ok(usuarioService.actualizarPorId(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado", "mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validación",
                    "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error al actualizar usuario {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al actualizar usuario", "detalle", e.getMessage()));
        }
    }

    // DELETE: eliminar por ID -> /api/usuarios/usuarios/{id}
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable Long id) {
        try {
            boolean eliminado = usuarioService.eliminarPorId(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar usuario {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al eliminar usuario", "detalle", e.getMessage()));
        }
    }
}
