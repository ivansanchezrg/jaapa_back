package com.jaapa_back.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaapa_back.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Manejador personalizado para errores de acceso denegado en el sistema.
 * Se encarga de procesar y dar formato a las respuestas cuando un usuario
 * intenta acceder a recursos sin los permisos necesarios.
 */

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Maneja las excepciones de acceso denegado y genera una respuesta JSON estructurada.
     * OJO ---> esta excepcion se activa solo cuando ya esta logueado y se intenta entrar a un ruta que no tiene permiso.
     * @param request La solicitud HTTP que generó la excepción
     * @param response La respuesta HTTP donde se escribirá el mensaje de error
     * @param ex La excepción de acceso denegado que se produjo
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        log.error("Error de acceso denegado: {}", ex.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ApiResponse errorResponse = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN)
                .message("No tiene permisos suficientes para realizar esta operación")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
