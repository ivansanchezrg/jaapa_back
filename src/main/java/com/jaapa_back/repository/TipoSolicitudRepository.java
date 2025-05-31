package com.jaapa_back.repository;

import com.jaapa_back.model.TipoSolicitud;
import com.jaapa_back.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Long> {

    /**
     * Recupera todas las entidades TipoSolicitud con sus entidades Tarifa asociadas cargadas de forma anticipada.
     * Utiliza JOIN FETCH para prevenir el problema de consultas N+1 que ocurre con la carga perezosa cuando
     * se accede a la propiedad tarifa de múltiples entidades TipoSolicitud.
     *
     * @return Lista de entidades TipoSolicitud con sus relaciones Tarifa inicializadas
     */
    @Query("SELECT ts FROM TipoSolicitud ts LEFT JOIN FETCH ts.tarifa")
    List<TipoSolicitud> findAllWithTarifa();

    /**
     * Busca un TipoSolicitud por su nombre.
     *
     * @param nombre El nombre del TipoSolicitud a buscar.
     * @return Un Optional que contiene el TipoSolicitud si se encuentra, o vacío si no se encuentra.
     */
    @Query("SELECT t FROM TipoSolicitud t WHERE t.nombre = :nombre")
    Optional<TipoSolicitud> findByNombre(String nombre);

    /**
     * Busca todos los usuarios asociados a solicitudes de un tipo específico.
     *-
     * Esta consulta permite obtener la lista de usuarios que han realizado solicitudes
     * de un determinado tipo, identificado por su nombre. La consulta elimina duplicados
     * usando DISTINCT para evitar que un mismo usuario aparezca múltiples veces si tiene
     * varias solicitudes del mismo tipo.
     *
     * @param nombreTipoSolicitud El nombre exacto del tipo de solicitud a buscar
     * @return Lista de usuarios distintos que tienen solicitudes del tipo especificado
     * @throws IllegalArgumentException si el nombre del tipo de solicitud es null
     */
    @Query("SELECT DISTINCT s.usuario FROM Solicitud s " +
            "JOIN s.tipoSolicitud ts " +
            "WHERE ts.nombre = :nombreTipoSolicitud")
    List<Usuario> findUsuariosByTipoSolicitudNombre(@Param("nombreTipoSolicitud") String nombreTipoSolicitud);
}
