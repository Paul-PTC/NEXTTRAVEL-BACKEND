package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Nucleo;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.UsuarioDTO;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Cloudinary.CloudinaryService;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Nucleo.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = {
        "http://127.0.0.1:5501","http://localhost:5501",
        "http://127.0.0.1:5500","http://localhost:5500",
        "http://127.0.0.1:5502","http://localhost:5502"
})
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cservice; // Inyecta el servicio de Cloudinary

    // Helper para construir Pageable
    private Pageable buildPageable(int page, int size, String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(dir, s[0])));
    }

    // Helper para manejar errores de validación
    private ResponseEntity<Map<String, Object>> handleValidationErrors(BindingResult result) {
        Map<String, String> fieldErrors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", "Datos de validación inválidos");
        errorResponse.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Helper para manejar errores de negocio
    private ResponseEntity<Map<String, String>> handleBusinessError(String message) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", message
        ));
    }

    @GetMapping("/UsuariosListar")
    public ResponseEntity<?> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombreUsuario,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String searchType
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<UsuarioDTO> resultPage;

        if (search != null && !search.isBlank()) {
            switch (searchType) {
                case "nombre":
                    resultPage = usuarioService.buscarPorNombre(search, pageable);
                    break;
                case "correo":
                    resultPage = usuarioService.buscarPorCorreo(search, pageable);
                    break;
                case "rol":
                    resultPage = usuarioService.buscarPorRol(search, pageable);
                    break;
                default:
                    resultPage = usuarioService.listar(pageable);
                    break;
            }
        } else {
            resultPage = usuarioService.listar(pageable);
        }
        return ResponseEntity.ok(resultPage);
    }

    // POST: crear un nuevo usuario
    @PostMapping("/UsuarioC")
    public ResponseEntity<?> crear(
            @RequestParam String nombreUsuario,
            @RequestParam String correo,
            @RequestParam String rol,
            @RequestParam String password,
            @RequestParam(value = "image", required = false) MultipartFile file
    ) {
        try {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNombreUsuario(nombreUsuario);
            dto.setCorreo(correo);
            dto.setRol(rol);
            dto.setPassword(password);


            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error interno al crear el usuario"
            ));
        }
    }

    // PUT: actualizar un usuario por ID
    @PutMapping("/UsuarioA/{id}")
    public ResponseEntity<?> actualizarPorId(
            @PathVariable Long id,
            @RequestParam String nombreUsuario,
            @RequestParam String correo,
            @RequestParam String rol,
            @RequestParam(required = false) String password,
            @RequestParam(value = "Foto_Url", required = false) String fotoUrl,
            @RequestParam(value = "image", required = false) MultipartFile file
    ) {
        try {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setIdUsuario(id);
            dto.setNombreUsuario(nombreUsuario);
            dto.setCorreo(correo);
            dto.setRol(rol);

            if (password != null && !password.isBlank()) {
                dto.setPassword(password);
            }

            return ResponseEntity.ok(usuarioService.actualizarPorId(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return handleBusinessError(e.getMessage());
        } catch (Exception e) {
            log.error("Error al actualizar usuario {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error interno al actualizar el usuario"
            ));
        }
    }

    // DELETE: eliminar un usuario por ID
    @DeleteMapping("/UsuarioE/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable Long id) {
        try {
            boolean eliminado = usuarioService.eliminarPorId(id);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", "error",
                        "message", "Usuario no encontrado"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Usuario eliminado correctamente"
            ));
        } catch (Exception e) {
            log.error("Error al eliminar usuario {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error interno al eliminar el usuario"
            ));
        }
    }
}