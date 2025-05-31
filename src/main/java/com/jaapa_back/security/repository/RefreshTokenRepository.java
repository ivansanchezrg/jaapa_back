package com.jaapa_back.security.repository;

import com.jaapa_back.model.UsuarioSistema;
import com.jaapa_back.security.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsuario(UsuarioSistema usuario);
}
