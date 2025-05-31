package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jaapa_back.annotations.ToUpperCase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa los diferentes tipos de solicitud que puede realizar un cliente.
 * Incluye información sobre costos por derecho de servicio (agua o alcantarillado),
 * condiciones y relación con la tarifa mensual asociada.
 */
@Entity
@Table(name = "jaapa_tipos_solicitud")
@Getter
@Setter
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tip_sol_id")
    private Long id;

    /**
     * Nombre del tipo de solicitud.
     */
    @ToUpperCase
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "tip_sol_nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción detallada del tipo de solicitud.
     */
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "tip_sol_descripcion", length = 500)
    private String descripcion;

    /**
     * Valor que paga el cliente por el derecho de solicitud o derecho de servicio.
     * Representa el costo inicial por adquirir el servicio de agua o alcantarillado.
     */
    @NotNull(message = "El costo no puede ser nulo")
    @Column(name = "tip_sol_costo", nullable = false)
    private Double costo;

    /**
     * Valor diferido inicial que puede aplicarse en caso de pagos en cuotas.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "El valor diferido inicial debe ser mayor o igual a 0")
    @Column(name = "tip_sol_valor_diferido_inicial")
    private Double valorDiferidoInicial;

    /**
     * Condiciones específicas que aplican a este tipo de solicitud.
     */
    @Column(name = "tip_sol_condiciones")
    private String condiciones;

    /**
     * Fecha en que se creó este tipo de solicitud en el sistema.
     */
    @NotNull(message = "La fecha de creación no puede ser nula")
    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    @Column(name = "tip_sol_fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    /**
     * Tarifa mensual asociada a este tipo de solicitud.
     * Contiene información sobre los pagos mensuales y consumo excedente.
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tar_id", unique = true)
    private Tarifa tarifa;
}
