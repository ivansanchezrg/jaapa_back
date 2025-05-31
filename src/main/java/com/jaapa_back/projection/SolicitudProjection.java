package com.jaapa_back.projection;

import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.EstadoSolicitudEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SolicitudProjection {
    private final Long id;
    private final String numeroSolicitud;
    private final LocalDate fecha;
    private final EstadoSolicitudEnum estado;
    private final EstadoPagoEnum estadoPago;
    private final BigDecimal montoTotal;
    private final BigDecimal montoPagado;
    private final BigDecimal montoPendiente;
    private final String nombreUsuario;
    private final String cedulaUsuario;
    private final String nombreTipoSolicitud;
}
