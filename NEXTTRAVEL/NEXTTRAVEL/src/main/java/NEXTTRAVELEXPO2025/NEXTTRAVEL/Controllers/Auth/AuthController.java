    package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Auth;

    import NEXTTRAVELEXPO2025.NEXTTRAVEL.Entities.Nucleo.Usuario;
    import NEXTTRAVELEXPO2025.NEXTTRAVEL.Models.DTO.Nucleo.UsuarioDTO;
    import NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Auth.AuthServices;
    import NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils.JWTUtils;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;

    import java.util.Collection;
    import java.util.Map;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        AuthServices authServices;
        @Autowired
        private JWTUtils jwtUtils;

        @PostMapping("/login")
        private ResponseEntity<String> login(@RequestBody UsuarioDTO data, HttpServletResponse response) {
            System.out.println("Adentro de AuthController");
            if (data.getCorreo() == null || data.getCorreo().isBlank() ||
                    data.getPassword() == null || data.getPassword().isBlank()) {
                return ResponseEntity.status(401).body("Error: Credenciales incompletas");
            }
            System.out.println("Pasamos las Validaciones");

            if (authServices.Login(data.getCorreo(), data.getPassword())) {
                addTokenCookie(response, data.getCorreo());// ← Pasar solo el correo
                //System.out.println("Si Contraseña Paso el inicio de Sesion Exitoso");
                return ResponseEntity.ok("Inicio de sesión exitoso");
            }
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        /**
         * Se genera el token y se guarda en la Cookie
         * @param response
         * @param
         */
        private void addTokenCookie(HttpServletResponse response, String correo) {
            Optional<Usuario> userOpt = authServices.obtenerUsuario(correo);
            System.out.println("Estamos ya agregando la Cookie");
            if (userOpt.isPresent()) {
                Usuario user = userOpt.get();
                String token = jwtUtils.create(
                        String.valueOf(user.getIdUsuario()),
                        user.getCorreo(),
                        user.getTipoUsuario().getNombreTipo()
                );
                System.out.println("Ya pasamos la Cookie");
                String cookieValue = String.format(
                        "authToken=%s; " +
                                "Path=/; " +
                                "HttpOnly; " +
                                "Secure=false; " +
                                "SameSite=None; " +
                                "MaxAge=86400; " ,
                                //"Domain=learnapifront-9de8a2348f9a.herokuapp.com",
                        token
                );

                response.addHeader("Set-Cookie", cookieValue);
                //response.addHeader("Access-Control-Allow-Credentials", "true"); <-- ESTO NO DEBEN AGREGARLO
                response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
            }
        }
        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser(Authentication authentication) {
            try {
                if (authentication == null || !authentication.isAuthenticated()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of(
                                    "authenticated", false,
                                    "message", "No autenticado"
                            ));
                }

                // Manejar diferentes tipos de Principal
                String username;
                Collection<? extends GrantedAuthority> authorities;

                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    username = userDetails.getUsername();
                    authorities = userDetails.getAuthorities();
                } else {
                    username = authentication.getName();
                    authorities = authentication.getAuthorities();
                }

                Optional<Usuario> userOpt = authServices.obtenerUsuario(username);

                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of(
                                    "authenticated", false,
                                    "message", "Usuario no encontrado"
                            ));
                }

                Usuario user = userOpt.get();

                return ResponseEntity.ok(Map.of(
                        "authenticated", true,
                        "user", Map.of(
                                "id", user.getIdUsuario(),
                                "nombre", user.getNombreUsuario(),
                                "correo", user.getCorreo(),
                                "rol", user.getTipoUsuario().getNombreTipo(),
                                "authorities", authorities.stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList())
                        )
                ));

            } catch (Exception e) {
                //log.error("Error en /me endpoint: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Error obteniendo datos de usuario"
                        ));
            }
        }
        @PostMapping("/logout")
        public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
            // Crear cookie de expiración con SameSite=None
            String cookieValue = "authToken=; Path=/; HttpOnly; Secure; SameSite=None; MaxAge=0; Domain=learnapifront-9de8a2348f9a.herokuapp.com";

            response.addHeader("Set-Cookie", cookieValue);
            //response.addHeader("Access-Control-Allow-Credentials", "true"); <-- ESTO NO DEBEN AGREGARLO
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");

            // También agregar headers CORS para la respuesta
            String origin = request.getHeader("Origin");
            if (origin != null &&
                    (origin.contains("localhost") || origin.contains("herokuapp.com"))) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
            return ResponseEntity.ok()
                    .body("Logout exitoso");
        }

    }
