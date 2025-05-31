package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.EstadoSolicitudEnum;
import com.jaapa_back.enums.TipoPagoSolicitudEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "jaapa_pagos_diferidos_solicitudes")
@Getter
@Setter
public class PagoDiferidoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pag_id")
    private Long id;

    @Column(name = "pag_fecha")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPago; // Fecha en la que se debe pagar cada cuota

    @Column(name = "pag_fecha_pagada")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPagada; // Fecha en la se cancela la deuda o cuota.

    @Column(name = "pag_monto")
    private Double montoPago;

    @Column(name = "pag_tipo" , length = 20)
    @Enumerated(EnumType.STRING)
    private TipoPagoSolicitudEnum tipoPagoSolicitudEnum;

    @Column(name = "pag_estado" , length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoPagoEnum estadoPagoEnum;

    @ManyToOne
    @JoinColumn(name = "sol_id_fk", referencedColumnName = "sol_id")
    private Solicitud solicitud;
}
