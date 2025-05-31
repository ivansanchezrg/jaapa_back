package com.jaapa_back.repository;


import com.jaapa_back.model.TiposActividad;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TiposActividadRepository extends CrudRepository<TiposActividad, Long> {

    /**
     * Busca un tipo de actividad por su nombre
     *
     * @param nombre El nombre del tipo de actividad a buscar
     * @return Optional con el tipo de actividad si existe
     */
    Optional<TiposActividad> findByNombre(String nombre);
}
