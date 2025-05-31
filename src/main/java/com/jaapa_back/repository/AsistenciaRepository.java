package com.jaapa_back.repository;

import com.jaapa_back.dto.response.AsistenciaListaResponseDTO;
import com.jaapa_back.dto.response.AsistenciaResumenDTO;
import com.jaapa_back.model.Asistencia;
import com.jaapa_back.projection.AsistenciaProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {


    /**
     * Obtiene todas las asistencias con información proyectada de forma paginada.
     * -
     * La consulta recupera información resumida de asistencias incluyendo:
     * - ID de asistencia
     * - Nombre del tipo de servicio
     * - Nombre del tipo de actividad
     * - Fecha de registro
     * - Estado de pago de multa
     * - Cantidad de detalles asociados
     * - Suma total de multas (0 si no hay multas)
     * -
     * Los resultados se agrupan por datos principales de asistencia.
     * El ordenamiento aplicado viene definido por el parámetro Pageable.
     * Por defecto (si no se especifica ordenamiento), se ordenará por ID
     * en orden descendente para mostrar los registros más recientes primero.
     *
     * @param pageable Objeto de paginación y ordenamiento
     * @return Una página de proyecciones de asistencia
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta")
    Page<AsistenciaProjection> findAllAsistenciasProjection(Pageable pageable);

    // =========================== CONSULTAS PARA EL FILTRADO POR FECHA =============================== /

    /**
     * Busca registros de asistencia por una fecha específica.
     *
     * @param fecha La fecha exacta para filtrar
     * @param pageable Objeto de paginación
     * @return Página de proyecciones filtrada por fecha exacta
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro = :fecha")
    Page<AsistenciaProjection> findProjectedByFechaRegistro(@Param("fecha") LocalDate fecha, Pageable pageable);

    /**
     * Busca registros de asistencia a partir de una fecha (exclusiva).
     *
     * @param fecha Fecha límite inferior (no incluida en los resultados)
     * @param pageable Objeto de paginación
     * @return Página de proyecciones con fecha estrictamente mayor a la especificada
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro > :fecha")
    Page<AsistenciaProjection> findProjectedByFechaRegistroGreaterThan(@Param("fecha") LocalDate fecha, Pageable pageable);

    /**
     * Busca registros de asistencia hasta una fecha (exclusiva).
     *
     * @param fecha Fecha límite superior (no incluida en los resultados)
     * @param pageable Objeto de paginación
     * @return Página de proyecciones con fecha estrictamente menor a la especificada
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro < :fecha")
    Page<AsistenciaProjection> findProjectedByFechaRegistroLessThan(@Param("fecha") LocalDate fecha, Pageable pageable);

    /**
     * Busca registros de asistencia dentro de un rango de fechas.
     *
     * @param fechaInicio Fecha inicial del rango (fecha menor, inclusive)
     * @param fechaFin Fecha final del rango (fecha mayor, inclusive)
     * @param pageable Objeto de paginación
     * @return Página de proyecciones dentro del rango especificado
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    Page<AsistenciaProjection> findProjectedByFechaRegistroBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, Pageable pageable);

    /**
     * Busca registros de asistencia donde la fecha es diferente a la especificada.
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro IS NOT NULL AND a.fechaRegistro <> :fecha")
    Page<AsistenciaProjection> findProjectedByFechaRegistroNotEqual(@Param("fecha") LocalDate fecha, Pageable pageable);

    /**
     * Busca registros de asistencia donde la fecha es nula.
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro IS NULL")
    Page<AsistenciaProjection> findProjectedByFechaRegistroIsNull(Pageable pageable);

    /**
     * Busca registros de asistencia donde la fecha no es nula.
     */
    @Query("SELECT new com.jaapa_back.projection.AsistenciaProjection(" +
            "a.id, ts.nombre, ta.nombre, a.fechaRegistro, a.estadoPagoMultaEnum, a.multaTotal, a.multaPagada, a.multaPendiente) " +
            "FROM Asistencia a " +
            "JOIN a.tipoServicio ts " +
            "JOIN a.tipoActividad ta " +
            "WHERE a.fechaRegistro IS NOT NULL")
    Page<AsistenciaProjection> findProjectedByFechaRegistroIsNotNull(Pageable pageable);

    // ======================= CONSULTAS PARA LOS CARD DE RESUMENES ============================ /

    /**
     * Obtiene un resumen de asistencias para una fecha específica.
     * Incluye conteos de asistencias y montos de multas.
     *
     * @param fecha La fecha para filtrar las asistencias
     * @return Un DTO con el resumen de conteos y montos monetarios
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro = :fecha")
    AsistenciaResumenDTO obtenerResumenPorFecha(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene un resumen de asistencias para un rango de fechas.
     * Incluye conteos de asistencias y montos de multas.
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Un DTO con el resumen de conteos y montos monetarios
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    AsistenciaResumenDTO obtenerResumenPorRangoFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene un resumen de asistencias desde una fecha específica.
     * Incluye conteos de asistencias y montos de multas.
     *
     * @param fecha La fecha desde la cual filtrar las asistencias
     * @return Un DTO con el resumen de conteos y montos monetarios
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro >= :fecha")
    AsistenciaResumenDTO obtenerResumenDesdeFecha(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene un resumen de asistencias hasta una fecha específica.
     * Incluye conteos de asistencias y montos de multas.
     *
     * @param fecha La fecha hasta la cual filtrar las asistencias
     * @return Un DTO con el resumen de conteos y montos monetarios
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro <= :fecha")
    AsistenciaResumenDTO obtenerResumenHastaFecha(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene un resumen excluyendo una fecha específica.
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro IS NOT NULL AND a.fechaRegistro <> :fecha")
    AsistenciaResumenDTO obtenerResumenPorFechaNotEqual(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene un resumen para registros con fecha nula.
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro IS NULL")
    AsistenciaResumenDTO obtenerResumenPorFechaIsNull();

    /**
     * Obtiene un resumen para registros con fecha no nula.
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad " +
            "WHERE a.fechaRegistro IS NOT NULL")
    AsistenciaResumenDTO obtenerResumenPorFechaIsNotNull();


    /**
     * Obtiene un resumen general de todas las asistencias.
     * Incluye conteos de asistencias y montos de multas.
     *
     * @return Un DTO con el resumen de conteos y montos monetarios
     */
    @Query("SELECT new com.jaapa_back.dto.response.AsistenciaResumenDTO(" +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PAGADO' THEN ad.multa ELSE 0 END), " +
            "SUM(CASE WHEN ad.estadoPagoMultaEnum = 'PENDIENTE' THEN ad.multa ELSE 0 END)) " +
            "FROM Asistencia a LEFT JOIN a.detalles ad")
    AsistenciaResumenDTO obtenerResumenGeneral();
}
