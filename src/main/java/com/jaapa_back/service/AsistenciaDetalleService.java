package com.jaapa_back.service;

import com.jaapa_back.dto.AsistenciaDTO;
import com.jaapa_back.dto.response.AsistenciaDetalleUsuarioDTO;
import com.jaapa_back.enums.EstadoAsistenciaEnum;
import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.model.Asistencia;
import com.jaapa_back.model.AsistenciaDetalle;
import com.jaapa_back.model.TiposActividad;
import com.jaapa_back.model.Usuario;
import com.jaapa_back.repository.AsistenciaDetalleRepository;
import com.jaapa_back.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsistenciaDetalleService {

    private final AsistenciaDetalleRepository asistenciaDetalleRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AsistenciaDetalleService(AsistenciaDetalleRepository asistenciaDetalleRepository, UsuarioRepository usuarioRepository) {
        this.asistenciaDetalleRepository = asistenciaDetalleRepository;
        this.usuarioRepository = usuarioRepository;
    }


    /**
     * Crea los detalles de asistencia para una asistencia específica.
     *
     * @param asistentesDTO lista de DTOs con información de cada asistente
     * @param asistencia entidad de asistencia a la que se asociarán los detalles
     * @param tipoActividad tipo de actividad que determina el valor de multa si aplica
     * @return lista de detalles de asistencia creados
     * @throws EntityNotFoundException si algún usuario referenciado no existe
     */
    @Transactional
    public List<AsistenciaDetalle> crearDetallesAsistencia(List<AsistenciaDTO.AsistenteDTO> asistentesDTO, Asistencia asistencia, TiposActividad tipoActividad) {

        List<AsistenciaDetalle> detalles = new ArrayList<>();

        for (AsistenciaDTO.AsistenteDTO asistenteDTO : asistentesDTO) {
            // Verificar que el usuario existe
            if (!usuarioRepository.existsById(asistenteDTO.getId())) {
                throw new EntityNotFoundException("Usuario no encontrado: " + asistenteDTO.getId());
            }

            AsistenciaDetalle detalle = mapearDetalleDesdeDTO(asistenteDTO, asistencia, tipoActividad);
            detalles.add(detalle); // Solo agregar a la lista, no guardar individualmente
        }

        return detalles;
    }

    /**
     * Mapea un DTO de asistente a una entidad de detalle de asistencia.
     *
     * @param asistenteDTO DTO con los datos del asistente
     * @param asistencia entidad de asistencia a la que se asociará el detalle
     * @param tipoActividad tipo de actividad que determina el valor de multa
     * @return detalle de asistencia creado
     */
    private AsistenciaDetalle mapearDetalleDesdeDTO(AsistenciaDTO.AsistenteDTO asistenteDTO, Asistencia asistencia, TiposActividad tipoActividad) {

        AsistenciaDetalle detalle = new AsistenciaDetalle();
        detalle.setUsuarioId(asistenteDTO.getId());
        detalle.setEstadoAsistenciaEnum(EstadoAsistenciaEnum.valueOf(asistenteDTO.getStatus().toUpperCase()));
        detalle.setHora(asistenteDTO.getTime());
        detalle.setAsistencia(asistencia);
        detalle.setEstadoPagoMultaEnum(asistenteDTO.getEstadoPagoMultaEnum());

        // Asignar multa según el estado de asistencia
        detalle.setMulta(detalle.getEstadoAsistenciaEnum() == EstadoAsistenciaEnum.AUSENTE ?
                tipoActividad.getValor() : 0.0);

        return detalle;
    }

    /**
     * Obtiene los detalles de una asistencia con información completa de usuario
     *
     * @param asistenciaId ID de la asistencia
     * @return Lista de detalles de asistencia con datos de usuario
     */
    @Transactional(readOnly = true)
    public List<AsistenciaDetalleUsuarioDTO> obtenerDetallesUsuariosPorAsistencia(Long asistenciaId) {
        List<AsistenciaDetalle> detalles = asistenciaDetalleRepository.findByAsistenciaId(asistenciaId);

        return detalles.stream()
                .map(detalle -> {
                    Usuario usuario = usuarioRepository.findById(detalle.getUsuarioId()).orElse(null);
                    return mapToDetalleUsuarioDTO(detalle, usuario);
                })
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad AsistenciaDetalle y Usuario a un objeto DTO integrado
     *
     * @param detalle Entidad AsistenciaDetalle
     * @param usuario Entidad Usuario asociada
     * @return DTO con información combinada de asistencia y usuario
     */
    private AsistenciaDetalleUsuarioDTO mapToDetalleUsuarioDTO(AsistenciaDetalle detalle, Usuario usuario) {
        AsistenciaDetalleUsuarioDTO dto = new AsistenciaDetalleUsuarioDTO();
        dto.setId(detalle.getId());
        dto.setEstadoAsistencia(detalle.getEstadoAsistenciaEnum());
        dto.setHora(detalle.getHora());
        dto.setMulta(detalle.getMulta());
        dto.setEstadoPagoMultaEnum(detalle.getEstadoPagoMultaEnum());

        if (usuario != null) {
            dto.setUsuarioId(usuario.getId());
            dto.setCedula(usuario.getCedula());
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setCelular(usuario.getCelular());
        }

        return dto;
    }

}
