package com.jaapa_back.dto;

import com.jaapa_back.enums.EstadoEnum;
import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import com.jaapa_back.model.Solicitud;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedidorDTO {
    private Long id;
    private String codigo;
    private String marca;
    private String modelo;
    private EstadoPagoEnum estadoPagoEnum;
    private EstadoEnum estado;
    private int ultimaLectura; // Esta variables se utiliza solo para la implemtacion.
    private TipoRegistroEnum tipoRegistro;
    private Solicitud solicitud;
}
