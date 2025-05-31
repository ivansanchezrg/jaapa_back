package com.jaapa_back.dto.request;

import com.jaapa_back.dto.DireccionDTO;
import com.jaapa_back.dto.MedidorDTO;
import com.jaapa_back.dto.TipoSolicitudDTO;
import com.jaapa_back.dto.UsuarioDTO;
import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.EstadoSolicitudEnum;
import com.jaapa_back.enums.TipoPagoSolicitudEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// ==================================================================================
// Request utilizado para enviar la solicitud registrada por el usuario desde la web
// =================================================================================
@Getter
@Setter
public class SolicitudRequestDTO {

    private Long id;
    private TipoPagoSolicitudEnum tipoPago;
    private LocalDate fecha;
    private EstadoPagoEnum estadoPago;
    private EstadoSolicitudEnum estado;
    private BigDecimal montoPagado;
    private BigDecimal montoPendiente;
    private BigDecimal montoTotal;
    private String numeroSolicitud;
    private TipoRegistroEnum tipoRegistro;


    private UsuarioDTO usuario;
    private DireccionDTO direccion;
    private TipoSolicitudDTO tipoSolicitud;
    private MedidorDTO medidor;
}
