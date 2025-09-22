package NEXTTRAVELEXPO2025.NEXTTRAVEL.Config.Security;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource; // Inyecta CorsConfigurationSource

    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ← Permite preflight requests
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()

                        //Endpoints de MANTENIMIENTO
                        .requestMatchers(HttpMethod.GET, "/api/mantenimientos/mantenimientos/listar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/mantenimientos/mantenimientosA").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/mantenimientos/mantenimientosU/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/mantenimientos/mantenimientosE/{id}").authenticated()

                        //Endpoints de RESERVAS
                        .requestMatchers(HttpMethod.GET, "/api/reservas/reservas/listar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservas/reservasC").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/reservasA/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/reservasD/{id}").authenticated()

                        //Endpoints de CLIENTE
                        .requestMatchers(HttpMethod.GET, "/api/clientes/ClientesListar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/clientes/ClientesC").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/clientes/ClientesA/{dui}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.DELETE, "/api/clientes/ClientesE/{dui}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")

                        //Endpoints de EMPLEADOS
                        .requestMatchers(HttpMethod.GET, "/api/empleados/EmpleadoListar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/empleados/EmpleadoC").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/empleados/EmpleadoA/{dui}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.DELETE, "/api/empleados/EmpleadoE/{dui}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")

                        //Endpoints de USUARIOS
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/UsuariosListar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/UsuarioC").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/UsuarioA/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/UsuarioE/{id}").authenticated()

                        //Endpoints de VEHICULOS
                        .requestMatchers(HttpMethod.GET, "/api/vehiculos/vehiculos/listar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/vehiculos/vehiculosC").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/vehiculos/vehiculosA/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehiculos/vehiculosE/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")

                        //Endpoints de GASTOS
                        .requestMatchers(HttpMethod.GET, "/api/gastos/gastos/listar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/gastos/gastosC").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.PUT, "/api/gastos/gastosU/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")
                        .requestMatchers(HttpMethod.DELETE, "/api/gastos/gastosE/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE","ROLE_EMPLEADO")

                        //Endpoints de GANANCIA
                        .requestMatchers(HttpMethod.GET, "/api/ganancias/ganancias/listar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/ganancias/gananciasC").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE")
                        .requestMatchers(HttpMethod.PUT, "/api/ganancias/gananciasA/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE")
                        .requestMatchers(HttpMethod.DELETE, "/api/ganancias/gananciasE/{id}").hasAnyAuthority("ROLE_ADMIN","ROLE_JEFE")
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
