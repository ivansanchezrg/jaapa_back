package com.jaapa_back.dto.response;

import com.jaapa_back.enums.EstadoAsistenciaEnum;
import com.jaapa_back.enums.EstadoPagoMultaEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsistenciaDetalleUsuarioDTO {
    private Long id;
    private Long usuarioId;
    private String cedula;
    private String nombre;
    private String apellido;
    private String celular;
    private EstadoAsistenciaEnum estadoAsistencia;
    private String hora;
    private Double multa;
    private EstadoPagoMultaEnum estadoPagoMultaEnum;
}
