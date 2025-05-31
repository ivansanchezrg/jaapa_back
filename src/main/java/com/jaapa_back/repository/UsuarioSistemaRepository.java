package com.jaapa_back.repository;

import com.jaapa_back.model.UsuarioSistema;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioSistemaRepository extends CrudRepository<UsuarioSistema, Long> {

    Optional<UsuarioSistema> findByUsername(String username);

}
