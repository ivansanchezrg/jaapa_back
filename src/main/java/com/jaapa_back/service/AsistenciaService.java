package com.jaapa_back.service;

import com.jaapa_back.dto.AsistenciaDTO;
import com.jaapa_back.dto.response.AsistenciaListaResponseDTO;
import com.jaapa_back.dto.response.AsistenciaResumenDTO;;
import com.jaapa_back.enums.EstadoPagoMultaEnum;
import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.model.Asistencia;
import com.jaapa_back.model.AsistenciaDetalle;
import com.jaapa_back.model.TipoSolicitud;
import com.jaapa_back.model.TiposActividad;
import com.jaapa_back.projection.AsistenciaProjection;
import com.jaapa_back.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class AsistenciaService {

    private final TiposActividadRepository tiposActividadRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AsistenciaDetalleService asistenciaDetalleService;
    private final AgGridService agGridService;

    public AsistenciaService(TiposActividadRepository tiposActividadRepository, TipoSolicitudRepository tipoSolicitudRepository,
                             AsistenciaRepository asistenciaRepository, UsuarioRepository usuarioRepository, AsistenciaDetalleService asistenciaDetalleService,
                             AgGridService agGridService){
        this.tiposActividadRepository = tiposActividadRepository;
        this.tipoSolicitudRepository = tipoSolicitudRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.asistenciaDetalleService = asistenciaDetalleService;
        this.agGridService = agGridService;
    }


    /**
     * Guarda una nueva asistencia y sus detalles a partir de un DTO.
     * -
     * Este método procesa el objeto DTO para:
     * 1. Buscar y asociar el tipo de solicitud correspondiente
     * 2. Buscar y asociar el tipo de actividad correspondiente
     * 3. Crear un registro principal de asistencia
     * 4. Crear todos los registros de detalle con los asistentes indicados
     *
     * @param asistenciaDTO DTO con la información de tipo de servicio,
     *                      tipo de actividad y lista de asistentes
     * @throws EntityNotFoundException si no se encuentra el tipo de solicitud o actividad
     */
    @Transactional
    public void guardarAsistencia(AsistenciaDTO asistenciaDTO) {
        // Validaciones
        if (asistenciaDTO == null || asistenciaDTO.getAttendees() == null) {
            throw new IllegalArgumentException("Los datos de asistencia son inválidos");
        }

        // Buscar tipos
        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findByNombre(asistenciaDTO.getServiceType())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de solicitud no encontrado: " + asistenciaDTO.getServiceType()));

        TiposActividad tipoActividad = tiposActividadRepository.findByNombre(asistenciaDTO.getActivityType())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de actividad no encontrado: " + asistenciaDTO.getActivityType()));

        // Crear la asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setTipoServicio(tipoSolicitud);
        asistencia.setTipoActividad(tipoActividad);
        asistencia.setFechaRegistro(LocalDate.now());
        asistencia.setEstadoPagoMultaEnum(asistenciaDTO.getEstadoPagoMultaEnum());

        // Crear detalles
        List<AsistenciaDetalle> detalles = asistenciaDetalleService.crearDetallesAsistencia(
                asistenciaDTO.getAttendees(), asistencia, tipoActividad);

        asistencia.setDetalles(detalles);
        // actualizar los totales
        asistencia.actualizarTotales();

        // Guardar
        asistenciaRepository.save(asistencia);
    }

    /**
     * Lista las asistencias de forma paginada aplicando filtros opcionales.
     *
     * @param pageable Objeto de paginación y ordenamiento. Si no se especifica un ordenamiento,
     *                 por defecto se ordenarán por ID en orden descendente (últimos registros primero).
     * @param fechaFrom Fecha desde la cual filtrar (inclusiva)
     * @param fechaTo Fecha hasta la cual filtrar (inclusiva)
     * @param fechaEquals Fecha exacta para filtrar
     * @param fechaNotEqual Fecha a excluir
     * @param fechaIsBlank Filtrar registros sin fecha
     * @param fechaIsNotBlank Filtrar registros con fecha
     * @return Página de DTOs de asistencia con la información formateada
     */
    @Transactional(readOnly = true)
    public Page<AsistenciaListaResponseDTO> listarAsistenciasPaginadas(
            Pageable pageable,
            String fechaFrom,
            String fechaTo,
            String fechaEquals,
            String fechaNotEqual,
            Boolean fechaIsBlank,
            Boolean fechaIsNotBlank) {

        // Aplicar ordenamiento por defecto si no hay orden especificado
        pageable = this.agGridService.aplicarOrdenamientoDefault(pageable);


        LocalDate dateFrom = agGridService.parseFecha(fechaFrom);
        LocalDate dateTo = agGridService.parseFecha(fechaTo);
        LocalDate dateEquals = agGridService.parseFecha(fechaEquals);
        LocalDate dateNotEqual = agGridService.parseFecha(fechaNotEqual);

        Page<AsistenciaProjection> proyecciones;

        if (Boolean.TRUE.equals(fechaIsBlank)) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroIsNull(pageable);
        } else if (Boolean.TRUE.equals(fechaIsNotBlank)) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroIsNotNull(pageable);
        } else if (dateNotEqual != null) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroNotEqual(dateNotEqual, pageable);
        } else if (dateEquals != null) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistro(dateEquals, pageable);
        } else if (dateFrom != null && dateTo != null) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroBetween(dateFrom, dateTo, pageable);
        } else if (dateFrom != null) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroGreaterThan(dateFrom, pageable);
        } else if (dateTo != null) {
            proyecciones = asistenciaRepository.findProjectedByFechaRegistroLessThan(dateTo, pageable);
        } else {
            proyecciones = asistenciaRepository.findAllAsistenciasProjection(pageable);
        }

        return proyecciones.map(this::mapProjectionToDTO);
    }

    /**
     * Convierte una proyección a un DTO
     */
    private AsistenciaListaResponseDTO mapProjectionToDTO(AsistenciaProjection projection) {
        AsistenciaListaResponseDTO dto = new AsistenciaListaResponseDTO();
        dto.setId(projection.getId());
        dto.setTipoServicio(projection.getTipoServicioNombre());
        dto.setTipoActividad(projection.getTipoActividadNombre());
        dto.setFechaRegistro(projection.getFechaRegistro());
        dto.setEstadoPagoMultaEnum(projection.getEstadoPagoMultaEnum());
        dto.setMultaTotal(projection.getMultaTotal());
        dto.setMultaPagada(projection.getMultaPagada());
        dto.setMultaPendiente(projection.getMultaPendiente());
        return dto;
    }

    /**
     * Obtiene un resumen de asistencias según los parámetros de fecha proporcionados.
     * -
     * Este método determina qué tipo de consulta de resumen ejecutar basado en las fechas proporcionadas.
     * Permite filtrar por fecha específica, rango de fechas, desde una fecha, hasta una fecha,
     * o un resumen general si no se proporciona ninguna fecha.
     *
     * @param fechaFrom Fecha inicio en formato String (yyyy-MM-dd). Si sólo se proporciona este parámetro,
     *                  se obtienen datos desde esta fecha en adelante.
     * @param fechaTo Fecha fin en formato String (yyyy-MM-dd). Si sólo se proporciona este parámetro,
     *                se obtienen datos hasta esta fecha.
     * @param fechaEquals Fecha específica en formato String (yyyy-MM-dd). Si se proporciona este parámetro,
     *                    tiene prioridad sobre los demás y retorna datos solo de esa fecha.
     * @return Un DTO con el resumen de asistencias, incluyendo conteos y montos monetarios
     */
    @Transactional(readOnly = true)
    public AsistenciaResumenDTO obtenerResumenAsistencias(
            String fechaFrom,
            String fechaTo,
            String fechaEquals,
            String fechaNotEqual,
            Boolean fechaIsBlank,
            Boolean fechaIsNotBlank) {

        // Convertir strings de fecha a LocalDate
        LocalDate dateFrom = fechaFrom != null ? LocalDate.parse(fechaFrom) : null;
        LocalDate dateTo = fechaTo != null ? LocalDate.parse(fechaTo) : null;
        LocalDate dateEquals = fechaEquals != null ? LocalDate.parse(fechaEquals) : null;
        LocalDate dateNotEqual = fechaNotEqual != null ? LocalDate.parse(fechaNotEqual) : null;

        // Usar filtros para consulta de resumen
        if (Boolean.TRUE.equals(fechaIsBlank)) {
            return asistenciaRepository.obtenerResumenPorFechaIsNull();
        } else if (Boolean.TRUE.equals(fechaIsNotBlank)) {
            return asistenciaRepository.obtenerResumenPorFechaIsNotNull();
        } else if (dateNotEqual != null) {
            return asistenciaRepository.obtenerResumenPorFechaNotEqual(dateNotEqual);
        } else if (dateEquals != null) {
            return asistenciaRepository.obtenerResumenPorFecha(dateEquals);
        } else if (dateFrom != null && dateTo != null) {
            return asistenciaRepository.obtenerResumenPorRangoFechas(dateFrom, dateTo);
        } else if (dateFrom != null) {
            return asistenciaRepository.obtenerResumenDesdeFecha(dateFrom);
        } else if (dateTo != null) {
            return asistenciaRepository.obtenerResumenHastaFecha(dateTo);
        } else {
            return asistenciaRepository.obtenerResumenGeneral();
        }
    }


}
