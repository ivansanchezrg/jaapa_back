package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.EstadoSolicitudEnum;
import com.jaapa_back.enums.TipoPagoSolicitudEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "jaapa_solicitudes")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sol_id")
    private Long id;

    //@NotNull(message = "El numero de solicitud es obligatorio")
    @Column(name = "sol_numero", unique = true)
    private String numeroSolicitud;

    @NotNull(message = "El tipo de pago es obligatorio")
    @Column(name = "sol_tipo_pago", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoPagoSolicitudEnum tipoPago;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "sol_fecha")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La fecha de solicitud no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "sol_estado", length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEnum estado;

    @NotNull(message = "El estado de pago es obligatorio")
    @Column(name = "sol_estado_pago", length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoPagoEnum estadoPago;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Column(name = "sol_tipo_registro", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoRegistroEnum tipoRegistro;

    //@NotNull(message = "El monto pagado es obligatorio")
    @Column(name = "sol_monto_pagado")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    private BigDecimal montoPagado;

    //@NotNull(message = "El monto pendiente es obligatorio")
    @Column(name = "sol_monto_pendiente")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pendiente no puede ser negativo")
    private BigDecimal montoPendiente;

    @NotNull(message = "El monto total es obligatorio")
    @Column(name = "sol_monto_total")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    private BigDecimal montoTotal;

    @Column(name = "sol_url_certificado_instalacion", length = 500)
    private String urlCertificadoInstalacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"medidores", "solicitudes"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    private TipoSolicitud tipoSolicitud;

    @OneToOne(cascade = CascadeType.ALL)
    private Direccion direccion;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("solicitud")
    private Medidor medidor;
}
