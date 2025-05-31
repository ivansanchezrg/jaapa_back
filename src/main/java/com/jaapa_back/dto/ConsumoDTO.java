package com.jaapa_back.dto;

import com.jaapa_back.enums.EstadoEnum;
import com.jaapa_back.enums.EstadoPagoEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ConsumoDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaCorte;
    private int valorAnterior;
    private int valorActual;
    private int consumo;
    private BigDecimal costoAdicional;
    private BigDecimal total;
    private EstadoEnum estado;
    private EstadoPagoEnum estadoPago;
    private TarifaDTO tarifa;

    private UsuarioDTO usuario;
    private MedidorDTO medidor;
}
