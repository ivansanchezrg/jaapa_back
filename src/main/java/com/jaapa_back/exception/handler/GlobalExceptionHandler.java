package com.jaapa_back.exception.handler;

import com.jaapa_back.dto.response.ApiResponse;
import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.exception.custom.UnauthorizedException;
import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.util.NoSuchElementException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que maneja globalmente las excepciones de la aplicación.
 * Proporciona respuestas de error consistentes y significativas para diferentes tipos de excepciones.
 */
@ControllerAdvice
public class GlobalExceptionHandler  {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Maneja excepciones de violación de restricciones de validación.
     *
     * @param ex Excepción con las violaciones de restricciones
     * @return Respuesta con estado 400 y detalles de cada violación
     * ---
     * Casos de uso:
     * - Validaciones de Bean Validation
     * - Restricciones a nivel de base de datos
     * - Validaciones de formato (email, números, etc.)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        ApiResponse apiResponse = new ApiResponse(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de argumentos de método.
     *
     * @param ex Excepción con errores de validación
     * @param request La solicitud web actual
     * @return Respuesta con estado 400 y detalles de cada error por campo
     *
     * Casos de uso:
     * - Validación de @RequestBody con anotaciones como @Valid
     * - Errores en DTOs o entidades anotadas con validaciones
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Usamos el nombre del campo como clave y el mensaje de error como valor
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

            // Si quieres incluir más información en el mensaje de error, podrías hacer algo así:
            // errors.put(fieldName, String.format("Valor '%s' %s", error.getRejectedValue(), apiResponse));
        });

        ApiResponse apiResponse = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message("Error de validación")
                .path(request.getDescription(false))
                .errors(errors)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja las excepciones de tipo UnauthorizedException.
     * Genera una respuesta estructurada cuando se producen errores de autorización específicos.
     *
     * @param ex La excepción UnauthorizedException que se produjo
     * @return ResponseEntity<ApiResponse> con el mensaje de error estructurado
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ApiResponse errorResponse = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(getCurrentPath())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Obtiene la ruta actual de la solicitud.
     *
     * @return String con la URI de la solicitud actual
     */
    private String getCurrentPath() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            return attributes.getRequest().getRequestURI();
        }
        return "";
    }

    /**
     * Maneja las excepciones de tipo ValorActualIncorrectoException.
     * Esta excepción se lanza cuando se detecta un valor incorrecto, generalmente
     * durante la validación de datos de entrada.
     *
     * @param exception La instancia de ValorActualIncorrectoException capturada
     * @return ResponseEntity<Object> con un estado HTTP CONFLICT (409) y un cuerpo
     *         que contiene detalles del error

     * Casos de uso:
     * 1. Cuando se intenta asignar un valor inválido a un enum.
     * 2. Cuando se realiza una validación personalizada y se detecta un valor incorrecto.
     *
     * Ejemplo: Al intentar convertir un string a un enum TipoPagoSolicitudEnum con un valor no válido.
     */
    @ExceptionHandler(ValorActualIncorrectoException.class)
    public ResponseEntity<ApiResponse> valorActualIncorrectoException(ValorActualIncorrectoException exception) {
        ApiResponse message = new ApiResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    /**
     * Manejador global para EntityNotFoundException.
     *
     * @param exception La excepción EntityNotFoundException capturada
     * @return ResponseEntity con información detallada sobre el error
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        ApiResponse message = new ApiResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    /**
     * Maneja excepciones cuando un recurso solicitado no existe.
     *
     * @param ex La excepción NoSuchElementException capturada
     * @return Respuesta con estado 404 y mensaje de error
     * ---
     * Casos de uso:
     * - Cuando se usa findById().orElseThrow()
     * - Cuando se intenta acceder a un elemento de un Optional vacío con .get()
     * - Búsquedas en base de datos que no devuelven resultados esperados
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse> handleNoSuchElementException(NoSuchElementException ex) {
        ApiResponse response = new ApiResponse(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones relacionadas con acceso a datos y base de datos.
     *
     * @param ex La excepción DataAccessException capturada
     * @return Respuesta con estado 500 y mensaje de error
     *---
     * Casos de uso:
     * - Errores de conexión a base de datos
     * - Violaciones de integridad referencial
     * - Errores de transacción
     * - Consultas mal formadas o problemas de sintaxis SQL
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDataAccessException(DataAccessException ex) {
        logger.error("Error de acceso a datos: ", ex);
        ApiResponse response = new ApiResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al acceder a la base de datos",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja excepciones de formato en el cuerpo de las peticiones HTTP.
     *
     * @param ex La excepción HttpMessageNotReadableException capturada
     * @return Respuesta con estado 400 y mensaje de error
     *---
     * Casos de uso:
     * - JSON mal formado en el cuerpo de la petición
     * - Tipos de datos incompatibles (string donde se espera número)
     * - Propiedades requeridas ausentes en JSON
     * - Errores en la deserialización de objetos
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiResponse response = new ApiResponse(
                HttpStatus.BAD_REQUEST,
                "Formato de petición incorrecto",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Manejador genérico para excepciones no controladas específicamente.
     * Actúa como última capa de seguridad para cualquier error imprevisto.
     *
     * @param ex La excepción genérica capturada
     * @return Respuesta con estado 500 y mensaje genérico
     * --
     * Casos de uso:
     * - Cualquier excepción no manejada por otros handlers
     * - Errores inesperados en el sistema
     * - Fallos en integraciones con servicios externos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Error no controlado: ", ex);
        ApiResponse response = new ApiResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja excepciones de acceso denegado cuando un usuario autenticado
     * intenta acceder a un recurso para el cual no tiene permisos suficientes.
     *
     * @param ex La excepción AccessDeniedException capturada
     * @return Respuesta con estado 403 y mensaje descriptivo
     *--
     * Casos de uso:
     * - Usuario con rol incorrecto intenta acceder a una funcionalidad restringida
     * - Intento de acceso a recursos protegidos por @PreAuthorize, @Secured, etc.
     * - Verificaciones de seguridad personalizadas que fallan
     */
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
//        ApiResponse response = new ApiResponse(
//                HttpStatus.FORBIDDEN,
//                "Acceso denegado: No tiene permisos suficientes para realizar esta acción",
//                LocalDateTime.now()
//        );
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }

}
