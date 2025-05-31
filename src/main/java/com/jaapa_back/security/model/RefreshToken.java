package com.jaapa_back.security.model;

import com.jaapa_back.model.UsuarioSistema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "usu_sis_id")
    private UsuarioSistema usuario;

    @Column(nullable = false)
    private Instant expiryDate;
}
