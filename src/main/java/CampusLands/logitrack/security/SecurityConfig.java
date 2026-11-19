package CampusLands.logitrack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration// Le digo a Spring que esta clase es de configuración
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyecto mi filtro JWT personalizado
    // Este filtro es el que va a leer el token en cada petición
    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * Aquí estoy configurando toda la seguridad de la aplicación.
         */
        return http
                // ESTA LÍNEA ES NUEVA PARA EL CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desactivo CSRF porque mi API es REST y trabaja con JWT (stateless).
                .csrf(csrf -> csrf.disable())

                // Le digo a Spring que no quiero sesiones.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Aquí defino qué endpoints son públicos y cuáles no.
                .authorizeHttpRequests(auth -> auth
                        // Permito acceso libre al login y al registro.
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        //Esta me permite acceso OPTIONS, desde el frotend, evitando el CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger queda publico para poder probar desde el navegador
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // ===== APROBACION DE REGISTROS: solo ADMIN =====
                        .requestMatchers("/auth/pendientes").hasRole("ADMIN")
                        .requestMatchers("/auth/aprobar/**").hasRole("ADMIN")
                        .requestMatchers("/auth/rechazar/**").hasRole("ADMIN")

                        // ===== MOVIMIENTOS: ADMIN y EMPLEADO =====
                        // El empleado existe justamente para registrar movimientos
                        .requestMatchers("/api/movimiento/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/api/detalle/**").hasAnyRole("ADMIN", "EMPLEADO")

                        // ===== AUDITORIA Y REPORTES: solo ADMIN =====
                        .requestMatchers("/api/auditoria/**").hasRole("ADMIN")
                        .requestMatchers("/api/reporte/**").hasRole("ADMIN")

                        // ===== GESTION DE BODEGAS, PRODUCTOS E INVENTARIO: solo ADMIN =====
                        .requestMatchers(HttpMethod.POST, "/api/bodega/**", "/api/producto/**", "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bodega/**", "/api/producto/**", "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/bodega/**", "/api/producto/**", "/api/inventario/**").hasRole("ADMIN")
                        // Consultar si lo puede hacer cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/bodega/**", "/api/producto/**", "/api/inventario/**").authenticated()

                        // Todo lo demás requiere autenticación.
                        .anyRequest().authenticated()
                )
                /*
                 * Aquí agrego mi filtro JWT antes del filtro
                 * UsernamePasswordAuthenticationFilter.
                 */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
