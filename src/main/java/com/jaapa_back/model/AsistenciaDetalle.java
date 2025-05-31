package com.jaapa_back.model;

import com.jaapa_back.enums.EstadoAsistenciaEnum;
import com.jaapa_back.enums.EstadoPagoMultaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "jaapa_asistencias_detalle")
@Getter
@Setter
@NoArgsConstructor
public class AsistenciaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asis_det_id")
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "asis_det_usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "asis_det_estado", nullable = false, length = 10)
    private EstadoAsistenciaEnum estadoAsistenciaEnum;

    @Column(name = "asis_det_hora", length = 8)
    @Size(max = 8, message = "La hora no debe exceder los 8 caracteres")
    private String hora;

    @Column(name = "asis_det_multa")
    private Double multa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asis_det_asistencia_id")
    private Asistencia asistencia;

    @NotNull(message = "El estado multa para detalles es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "asis_det_estado_multa", nullable = false, length = 10)
    private EstadoPagoMultaEnum estadoPagoMultaEnum;

    @Column(name = "asis_det_fecha_pago_multa")
    private LocalDate fechaPagoMulta;
}
