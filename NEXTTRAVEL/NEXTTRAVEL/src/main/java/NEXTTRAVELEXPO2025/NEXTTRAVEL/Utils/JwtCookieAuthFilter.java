package NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);
    private static final String AUTH_COOKIE_NAME = "authToken";
    private final JWTUtils jwtUtils;

    @Autowired
    public JwtCookieAuthFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // CORREGIDO: Mejor lógica para endpoints públicos
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            System.out.println("Creacion de la Cookie");
            String token = extractTokenFromCookies(request);

            if (token == null || token.isBlank()) {
                // Para endpoints no públicos, requerimos token
                if (!isPublicEndpoint(request)) {
                    sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            }
            System.out.println("Cookie paso el fitlro Interno");

            Claims claims = jwtUtils.parseToken(token);

            // EXTRAER EL ROL REAL del token
            String rol = jwtUtils.extractRol(token);
            //Cliente
            //ROLE_Cliente

            // CREAR AUTHORITIES BASADO EN EL ROL REAL
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
            System.out.println("Creando basado en el rol");

            // CREAR AUTENTICACIÓN CON AUTHORITIES CORRECTOS
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), // username
                            null, // credentials
                            authorities // ← ROLES REALES
                    );
            System.out.println("Autenticacion Autorities");
            // ESTABLECER AUTENTICACIÓN EN CONTEXTO
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        System.out.println("Ya Terminamos la Cookie");
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        System.out.println("Dentro de la Cookie ");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        System.out.println("Extrayendo el Token de la Cookie");

        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": %d}", message, status));
    }

    // MEJORADA: Lógica para endpoints públicos
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("Creando EndPoints Publicos");
        // Endpoints públicos
        return (path.equals("/api/auth/login") && "POST".equals(method)) ||
                (path.equals("/api/auth/register") && "POST".equals(method)) ||
                (path.equals("/api/public/") && "GET".equals(method));
    }
}
