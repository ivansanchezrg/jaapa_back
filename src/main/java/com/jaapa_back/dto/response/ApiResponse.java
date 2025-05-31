package com.jaapa_back.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * Clase que representa un mensaje de error personalizado.
 * Se utiliza para estructurar la respuesta de error enviada al cliente.
 */
@Getter @Setter
@Builder
@AllArgsConstructor
public class ApiResponse {
    private HttpStatus status;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;
    private String token;
    private String refreshToken;
    private Map<String, String> errors;
    private Set<String> roles;


    // Para errores de validaciones
    public ApiResponse(HttpStatus status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    // Para Errores generales
    public ApiResponse(HttpStatus status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = null; // Puedes asignar null o inicializar el mapa si lo prefieres
    }
}

