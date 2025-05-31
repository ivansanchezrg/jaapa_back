package com.jaapa_back.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar casos específicos de autorización
 * en la lógica de negocio de la aplicación.
 */

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException{

    /**
     * Constructor para crear una excepción con un mensaje específico.
     *
     * @param message Mensaje que describe la razón de la falta de autorización
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructor para crear una excepción con un mensaje y una causa.
     *
     * @param message Mensaje que describe la razón de la falta de autorización
     * @param cause La causa original de la excepción
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

