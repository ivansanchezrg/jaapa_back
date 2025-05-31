package com.jaapa_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TipoSolicitudDTO {

    private Long tip_sol_id;
    private String nombre;
    private String descripcion;
    private BigDecimal costo;
    private BigDecimal valorDiferidoInicial;
    private LocalDate fechaCreacion;
    private boolean requiereMedidor;

}

