package com.jaapa_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para presentar el resumen de asistencias con conteos importantes.
 *-
 * IMPORTANTE: Este DTO requiere un constructor específico de 3 parámetros
 * para funcionar con consultas JPQL que usan "new com.jaapa_back.dto.response.AsistenciaResumenDTO()"
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResumenDTO {
    // Valores monetarios
    private Double montoPagado;
    private Double montoPendiente;
    private Double montoTotal;

    /**
     * Constructor para consultas JPQL con solo montos
     */
    public AsistenciaResumenDTO(Double montoPagado, Double montoPendiente) {
        this.montoPagado = montoPagado != null ? montoPagado : 0.0;
        this.montoPendiente = montoPendiente != null ? montoPendiente : 0.0;
        this.montoTotal = this.montoPagado + this.montoPendiente;
    }
}