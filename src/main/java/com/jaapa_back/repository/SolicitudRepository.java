package com.jaapa_back.repository;

import com.jaapa_back.dto.SolicitudDetalleDTO;
import com.jaapa_back.dto.response.RequestSummary;
import com.jaapa_back.enums.EstadoSolicitudEnum;
import com.jaapa_back.model.Solicitud;
import com.jaapa_back.projection.SolicitudProjection;
import com.jaapa_back.projection.SolicitudSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /* ==================================================================
     *  CONSULTAS PARA LISTADO DE SOLICITUDES GENERAL
     * =================================================================== */
    /**
     * Consulta paginada que retorna proyecciones optimizadas de Solicitud
     * -
     * Esta consulta utiliza una proyección para evitar cargar entidades completas.
     * Solo recupera los campos necesarios para el listado en grid, incluyendo
     * información básica del usuario asociado.
     *
     * @param pageable Configuración de paginación
     * @return Page<SolicitudDTO> Página de DTOs de solicitudes
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t" )
    Page<SolicitudProjection> findAllProjectedPaginated(Pageable pageable);


    /* ==================================================================
     *  CONSULTAS PARA FILTRAR POR ESTADO
     * =================================================================== */
    /**
     * Busca solicitudes por estado exacto
     * @param pageable configuración de paginación
     * @param estado valor del enum EstadoSolicitudEnum
     * @return página de solicitudes que coinciden exactamente con el estado
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.estado = :estado")
    Page<SolicitudProjection> findAllProjectedByEstado(Pageable pageable, @Param("estado") EstadoSolicitudEnum estado);

    /**
     * Busca solicitudes donde el estado contenga el texto especificado (case-insensitive)
     * @param pageable configuración de paginación
     * @param estado texto parcial a buscar en el estado
     * @return página de solicitudes cuyo estado contiene el texto
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE UPPER(s.estado) LIKE UPPER(CONCAT('%', :estado, '%'))")
    Page<SolicitudProjection> findAllProjectedByEstadoContains(Pageable pageable, @Param("estado") String estado);

    /* ==================================================================
     *  CONSULTAS PARA FILTRAR POR NÚMERO DE SOLICITUD
     * =================================================================== */

    /**
     * Busca solicitudes por número de solicitud exacto
     * @param pageable configuración de paginación
     * @param numeroSolicitud número exacto de la solicitud
     * @return página de solicitudes con el número especificado
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.numeroSolicitud = :numeroSolicitud")
    Page<SolicitudProjection> findAllProjectedByNumeroSolicitud(Pageable pageable, @Param("numeroSolicitud") String numeroSolicitud);

    /* ==================================================================
     *  CONSULTAS PARA FILTRAR POR NÚMERO DE CEDULA DE USUARIO
     * =================================================================== */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE u.cedula = :cedulaUsuario")
    Page<SolicitudProjection> findAllProjectedByCedulaUsuario(Pageable pageable, @Param("cedulaUsuario") String cedulaUsuario);


    /* ==================================================================
     *  CONSULTAS PARA FILTRA FECHAS (IGUAL A, ANTES, DESPUES, ENTRE)
     * =================================================================== */
    /**
     * Busca solicitudes con fecha exacta
     * @param fecha fecha exacta a buscar
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.fecha = :fecha")
    Page<SolicitudProjection> findAllProjectedByFechaEquals(Pageable pageable, @Param("fecha") LocalDate fecha);

    /**
     * Busca solicitudes con fecha anterior a la especificada
     * @param fecha fecha límite superior
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.fecha < :fecha")
    Page<SolicitudProjection> findAllProjectedByFechaBefore(Pageable pageable, @Param("fecha") LocalDate fecha);

    /**
     * Busca solicitudes con fecha posterior a la especificada
     * @param fecha fecha límite inferior
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.fecha > :fecha")
    Page<SolicitudProjection> findAllProjectedByFechaAfter(Pageable pageable, @Param("fecha") LocalDate fecha);

    /**
     * Busca solicitudes con fecha entre dos valores
     * @param fechaFrom fecha inicial del rango
     * @param fechaTo fecha final del rango
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudProjection(" +
            "s.id, s.numeroSolicitud, s.fecha, s.estado, s.estadoPago, " +
            "s.montoTotal, s.montoPagado, s.montoPendiente, " +
            "u.nombre, u.cedula, t.nombre) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "JOIN s.tipoSolicitud t " +
            "WHERE s.fecha BETWEEN :fechaFrom AND :fechaTo")
    Page<SolicitudProjection> findAllProjectedByFechaBetween(Pageable pageable,
                                                             @Param("fechaFrom") LocalDate fechaFrom,
                                                             @Param("fechaTo") LocalDate fechaTo);



    /* ==================================================================
     *  CONSULTAS PARA RESUMEN GENERAL
     * =================================================================== */
    /**
     * Obtiene un resumen estadístico completo de todas las solicitudes.
     * Incluye totales monetarios y contadores por cada estado.
     *
     * @return Proyección con el resumen de solicitudes
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s")
    SolicitudSummaryProjection findSummary();

    /* ==================================================================
     *  CONSULTAS PARA RESUMEN POR ESTADO
     * =================================================================== */
    /**
     * Obtiene un resumen estadístico de solicitudes filtradas por estado específico.
     *
     * @param estado Estado para filtrar
     * @return Proyección con resumen filtrado
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.estado = :estado")
    SolicitudSummaryProjection findSummaryByEstado(@Param("estado") EstadoSolicitudEnum estado);

    /**
     * Resumen con filtro contains para estado
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE UPPER(CAST(s.estado AS string)) LIKE UPPER(CONCAT('%', :estado, '%'))")
    SolicitudSummaryProjection findSummaryByEstadoContains(@Param("estado") String estado);

    /* ==================================================================
     *  CONSULTAS PARA RESUMEN POR FECHA
     * =================================================================== */
    // Fecha exacta
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.fecha = :fecha")
    SolicitudSummaryProjection findSummaryByFechaEquals(@Param("fecha") LocalDate fecha);

    // Rango de fechas
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.fecha BETWEEN :from AND :to")
    SolicitudSummaryProjection findSummaryByFechaBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Después de fecha (excluye fecha from)
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.fecha > :from")
    SolicitudSummaryProjection findSummaryByFechaAfter(@Param("from") LocalDate from);

    // Antes de fecha (excluye fecha to)
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.fecha < :to")
    SolicitudSummaryProjection findSummaryByFechaBefore(@Param("to") LocalDate to);

    /* ==================================================================
     *  CONSULTAS PARA RESUMEN POR NÚMERO DE SOLICITUD y POR NUMERO DE CEDULA DEL USUARIO
     * =================================================================== */
    /**
     * Obtiene resumen de solicitudes filtradas por número de solicitud exacto
     * @param numeroSolicitud número exacto de la solicitud
     * @return resumen con totales y conteos de estados para solicitudes con ese número
     */
    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "WHERE s.numeroSolicitud = :numeroSolicitud")
    SolicitudSummaryProjection findSummaryByNumeroSolicitud(@Param("numeroSolicitud") String numeroSolicitud);

    @Query("SELECT new com.jaapa_back.projection.SolicitudSummaryProjectionImpl(" +
            "COUNT(s), " +
            "COALESCE(SUM(s.montoPagado), 0), " +
            "COALESCE(SUM(s.montoPendiente), 0), " +
            "COALESCE(SUM(s.montoTotal), 0), " +
            "SUM(CASE WHEN s.estado = 'APROBADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'RECHAZADA' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'EN_PROCESO' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.estado = 'COMPLETADA' THEN 1 ELSE 0 END)) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "WHERE u.cedula = :cedulaUsuario")
    SolicitudSummaryProjection findSummaryByCedulaUsuario(@Param("cedulaUsuario") String cedulaUsuario);


    /* ==================================================================
     *  CONSULTA PARA OBTENER EL DETALLE DE SOLICITUD
     * =================================================================== */
    /**
     * Consulta que recupera información detallada de una solicitud, incluyendo:
     * - Datos del usuario asociado (id, cedula, nombre, apellido, teléfono, celular, correo)
     * - Información de la dirección (id, calle principal, calle secundaria, referencia, barrio)
     * - Detalles del medidor (id, código, marca, modelo, estado)
     *
     * @param solicitudId El ID de la solicitud a consultar
     * @return Un objeto DTO con todos los detalles de la solicitud
     */
    @Query("SELECT new com.jaapa_back.dto.SolicitudDetalleDTO(" +
            "u.id, u.cedula, u.nombre, u.apellido, u.telefono, u.celular, u.correo, " +
            "d.id, d.callePrincipal, d.calleSecundaria, d.referencia, d.barrio, " +
            "m.id, m.codigo, m.marca, m.modelo, m.estado, " +
            "s.urlCertificadoInstalacion) " +
            "FROM Solicitud s " +
            "JOIN s.usuario u " +
            "LEFT JOIN s.direccion d " +
            "LEFT JOIN s.medidor m " +
            "WHERE s.id = :solicitudId")
    SolicitudDetalleDTO findSolicitudDetalleCompletoById(@Param("solicitudId") Long solicitudId);

    /**
     * Busca una solicitud por su número único
     * @param numeroSolicitud número de solicitud a buscar
     * @return Optional con la solicitud encontrada o vacío si no existe
     */
    Optional<Solicitud> findByNumeroSolicitud(String numeroSolicitud);
}
