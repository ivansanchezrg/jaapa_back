package com.jaapa_back.dto.response;


import com.jaapa_back.enums.EstadoPagoMultaEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AsistenciaListaResponseDTO {
    private Long id;
    private String tipoServicio;
    private String tipoActividad;
    private LocalDate fechaRegistro;
    private EstadoPagoMultaEnum estadoPagoMultaEnum;
    private double multaTotal;
    private double multaPagada;
    private double multaPendiente;
}
