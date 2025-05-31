package com.jaapa_back.repository;

import com.jaapa_back.model.Solicitud;
import com.jaapa_back.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su cédula.
     * @param cedula La cédula del usuario a buscar.
     * @return Un objeto Usuario si se encuentra, de lo contrario null.
     */
    @Query(value = "SELECT u FROM Usuario u WHERE u.cedula = :cedula")
    Usuario findUserByCedula(String cedula);
}
