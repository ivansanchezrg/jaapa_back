package com.jaapa_back.model;

import com.jaapa_back.enums.EstadoPagoMultaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jaapa_asistencias", indexes = {
        @Index(name = "idx_asistencia_fecha_registro", columnList = "asis_fecha_registro")
})
@Getter
@Setter
@NoArgsConstructor
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_sol_id")
    @NotNull(message = "El tipo de servicio es obligatorio")
    private TipoSolicitud tipoServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_id")
    @NotNull(message = "El tipo de actividad es obligatorio")
    private TiposActividad tipoActividad;

    @Column(name = "asis_fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @NotNull(message = "El estado multa es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "asis_det_estado_multa", nullable = false, length = 10)
    private EstadoPagoMultaEnum estadoPagoMultaEnum;

    @Column(name = "asis_multa_total")
    private Double multaTotal;

    @Column(name = "asis_multa_pagada")
    private Double multaPagada;

    @Column(name = "asis_multa_pendiente")
    private Double multaPendiente;

    @OneToMany(mappedBy = "asistencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AsistenciaDetalle> detalles;

    // Método para actualizar totales
    public void actualizarTotales() {
        this.multaTotal = detalles.stream().mapToDouble(AsistenciaDetalle::getMulta).sum();
        this.multaPagada = detalles.stream()
                .filter(d -> d.getEstadoPagoMultaEnum() == EstadoPagoMultaEnum.PAGADA)
                .mapToDouble(AsistenciaDetalle::getMulta).sum();
        this.multaPendiente = this.multaTotal - this.multaPagada;

        // Actualiza el estado global basado en los pagos
//        if (this.multaPendiente <= 0) {
//            this.estadoPagoMultaEnum = EstadoPagoMultaEnum.PAGADA;
//        } else if (this.multaPagada > 0) {
//            this.estadoPagoMultaEnum = EstadoPagoMultaEnum.PARCIAL;
//        } else {
//            this.estadoPagoMultaEnum = EstadoPagoMultaEnum.PENDIENTE;
//        }
    }
}
