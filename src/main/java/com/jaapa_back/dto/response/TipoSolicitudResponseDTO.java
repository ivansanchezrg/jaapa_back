package com.jaapa_back.dto.response;

import com.jaapa_back.dto.TarifaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TipoSolicitudResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double costo;
    private Double valorDiferidoInicial;
    private String condiciones;
    private LocalDate fechaCreacion;
    private TarifaDTO tarifa;
}
