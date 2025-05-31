package com.jaapa_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TarifaDTO {
    private Long id;
    private String nombre;
    private Double tarifaBasica;
    private Double tarifaAdicional;
    private int limiteMaximoConsumo;
    private LocalDate fechaInicio;
    private String descripcion;
}
