package com.jaapa_back.repository;

import com.jaapa_back.model.AsistenciaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AsistenciaDetalleRepository extends JpaRepository<AsistenciaDetalle, Long> {


    /**
     * Encuentra todos los detalles de asistencia asociados a un ID de asistencia específico
     *
     * @param asistenciaId ID de la asistencia
     * @return Lista de detalles de asistencia
     */
    @Query("SELECT d FROM AsistenciaDetalle d WHERE d.asistencia.id = :asistenciaId")
    List<AsistenciaDetalle> findByAsistenciaId(@Param("asistenciaId") Long asistenciaId);
}
