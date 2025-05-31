package com.jaapa_back.security.controller;

import com.jaapa_back.dto.response.ApiResponse;
import com.jaapa_back.exception.custom.TokenRefreshException;
import com.jaapa_back.model.UsuarioSistema;
import com.jaapa_back.repository.RoleRepository;
import com.jaapa_back.repository.UsuarioSistemaRepository;
import com.jaapa_back.security.model.LoginRequest;
import com.jaapa_back.security.jwt.JwtService;
import com.jaapa_back.security.model.RefreshToken;
import com.jaapa_back.security.model.TokenRefreshRequest;
import com.jaapa_back.security.service.RefreshTokenService;
import com.jaapa_back.security.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador para la autenticación y registro de usuarios.
 * Gestiona el inicio de sesión y creación de nuevos usuarios.
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RoleRepository rolRepository;
    private final RefreshTokenService refreshTokenService;



    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            try {
                userDetailsService.loadUserByUsername(request.getUsername());
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message(e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .path("/auth/login")
                                .build());
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());

            // Agregamos información del rol al response
            UsuarioSistema usuario = usuarioSistemaRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            Set<String> roles = usuario.getRoles().stream()
                    .map(rol -> rol.getNombre().name())
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Login exitoso")
                    .timestamp(LocalDateTime.now())
                    .path("/auth/login")
                    .token(token)
                    .roles(roles)  // Agregamos los roles en la respuesta
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .message("Contraseña incorrecta")
                            .timestamp(LocalDateTime.now())
                            .path("/auth/login")
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsuario().getUsername());
                    String newToken = jwtService.generateToken(userDetails);

                    return ResponseEntity.ok(ApiResponse.builder()
                            .status(HttpStatus.OK)
                            .message("Token refrescado exitosamente")
                            .timestamp(LocalDateTime.now())
                            .path("/auth/refresh-token")
                            .token(newToken)
                            .refreshToken(requestRefreshToken)
                            .build());
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token no encontrado"));
    }
}
