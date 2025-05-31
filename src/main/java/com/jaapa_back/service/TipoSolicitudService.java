package com.jaapa_back.service;

import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import com.jaapa_back.model.TipoSolicitud;
import com.jaapa_back.model.Usuario;
import com.jaapa_back.repository.TipoSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoSolicitudService {

    private final TipoSolicitudRepository tipoSolicitudRepository;

    @Autowired
    public TipoSolicitudService(TipoSolicitudRepository tipoSolicitudRepository) {
        this.tipoSolicitudRepository = tipoSolicitudRepository;
    }


    /**
     * Retorna todos los tipos de solicitudes.
     *
     * @return Iterable de TipoSolicitud.
     */
    @Transactional(readOnly = true)
    public List<TipoSolicitud> findAll() {
        return tipoSolicitudRepository.findAllWithTarifa();
    }

    /**
     * Obtiene un TipoSolicitud por su nombre.
     *
     * @param nombre El nombre del TipoSolicitud a buscar.
     * @return El TipoSolicitud encontrado.
     * @throws EntityNotFoundException si el TipoSolicitud no se encuentra.
     */
    public TipoSolicitud getTipoSolicitudByNombre(String nombre) throws EntityNotFoundException {
        Optional<TipoSolicitud> tipoSolicitud = tipoSolicitudRepository.findByNombre(nombre);
        return tipoSolicitud.orElseThrow(() -> new EntityNotFoundException("Tipo de solicitud no encontrado con nombre: " + nombre));
    }

    /**
     * Obtiene un tipoSolicitud por su ID.
     *
     * @param id ID del tipo de Solicitud a obtener.
     * @return Tipo Solicitud con el ID especificado o null si no existe.
     */
    public TipoSolicitud findById(Long id) throws EntityNotFoundException {
        return tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Solicitud no encontrado con ID: " + id));
    }

    /**
     * Recupera todos los usuarios que han realizado solicitudes de un tipo específico.
     * -
     * Este método utiliza el repositorio de solicitudes para obtener los usuarios
     * asociados a un determinado tipo de solicitud, identificado por su nombre.
     * Los resultados son filtrados para eliminar duplicados.
     *
     * @param nombreTipoSolicitud El nombre exacto del tipo de solicitud a consultar
     * @return Lista de usuarios que tienen al menos una solicitud del tipo especificado
     * @throws IllegalArgumentException si el parámetro nombreTipoSolicitud es null o vacío
     * @throws EntityNotFoundException si no existe un tipo de solicitud con el nombre especificado
     */
    public List<Usuario> obtenerUsuariosPorTipoSolicitud(String nombreTipoSolicitud) {
        // Validación de parámetros
        if (nombreTipoSolicitud == null || nombreTipoSolicitud.trim().isEmpty()) {
            throw new ValorActualIncorrectoException("El nombre del tipo de solicitud no puede ser nulo o vacío");
        }

        // Verificar si existe el tipo de solicitud usando el método findByNombre
        Optional<TipoSolicitud> tipoSolicitudOpt = tipoSolicitudRepository.findByNombre(nombreTipoSolicitud);
        if (tipoSolicitudOpt.isEmpty()) {
            throw new EntityNotFoundException("No existe un tipo de solicitud con el nombre: " + nombreTipoSolicitud);
        }

        // Ejecutar la consulta
        return tipoSolicitudRepository.findUsuariosByTipoSolicitudNombre(nombreTipoSolicitud);
    }
}
