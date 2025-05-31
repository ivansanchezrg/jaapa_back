package com.jaapa_back.security.config;

import com.jaapa_back.enums.RoleEnum;
import com.jaapa_back.exception.handler.CustomAccessDeniedHandler;
import com.jaapa_back.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.core.env.Environment;
import java.util.Arrays;

import static org.springframework.http.HttpMethod.POST;

/**
 * Configuración de seguridad del sistema.
 * Maneja la autenticación y autorización de usuarios.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // Endpoints PÚBLICOS
                        .requestMatchers(HttpMethod.GET, "/tipos-solicitud/**").hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/solicitudes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/solicitudes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuarios/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        String allowedOrigins = environment.getProperty("cors.allowed-origins", "http://localhost:4200");
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        String allowedMethods = environment.getProperty("cors.allowed-methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

        String allowedHeaders = environment.getProperty("cors.allowed-headers", "*");
        if ("*".equals(allowedHeaders)) {
            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        } else {
            config.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }

        String maxAgeStr = environment.getProperty("cors.max-age", "3600");
        config.setMaxAge(Long.parseLong(maxAgeStr));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
