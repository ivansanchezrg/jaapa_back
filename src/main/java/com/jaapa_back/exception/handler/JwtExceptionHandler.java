package com.jaapa_back.exception.handler;

import com.jaapa_back.dto.response.ApiResponse;
import com.jaapa_back.exception.custom.TokenRefreshException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.security.SignatureException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(extractPath(request))
                        .build());
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class, UnsupportedJwtException.class})
    public ResponseEntity<ApiResponse> handleJwtException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message("Token inválido: " + ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(extractPath(request))
                        .build());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message("Su sesión ha expirado. Por favor, inicie sesión nuevamente.")
                        .timestamp(LocalDateTime.now())
                        .path(extractPath(request))
                        .build());
    }

    private String extractPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
