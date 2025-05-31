package com.jaapa_back.projection;


import com.jaapa_back.enums.EstadoPagoMultaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;


/**
 * Proyección para optimizar consultas de listado de asistencias
 * -
 * Esta interfaz define los campos necesarios para el listado de asistencias
 * sin cargar entidades completas, reduciendo significativamente el consumo
 * de memoria y mejorando el rendimiento en consultas de gran volumen.
 *
 * @see com.jaapa_back.model.Asistencia
 * @see com.jaapa_back.dto.response.AsistenciaListaResponseDTO
 */
@Getter
@AllArgsConstructor
public class AsistenciaProjection {
    private Long id;
    private String tipoServicioNombre;
    private String tipoActividadNombre;
    private LocalDate fechaRegistro;
    private EstadoPagoMultaEnum estadoPagoMultaEnum;
    private Double multaTotal;
    private Double multaPagada;
    private Double multaPendiente;
}
