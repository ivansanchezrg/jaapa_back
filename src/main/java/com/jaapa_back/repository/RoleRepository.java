package com.jaapa_back.repository;

import com.jaapa_back.enums.RoleEnum;
import com.jaapa_back.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un Rol por su nombre.
     *
     * @param nombre El nombre del Rol a buscar.
     * @return Un Optional que contiene el Rol si se encuentra, o vacío si no se encuentra.
     */
    @Query("SELECT t FROM Role t WHERE t.nombre = :nombre")
    Optional<Role> findByNombre(RoleEnum nombre);
}
