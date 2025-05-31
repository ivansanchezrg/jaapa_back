package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


/**
 * Entidad que representa el esquema tarifario asociado a un tipo de solicitud.
 * Define las tarifas mensuales básicas y adicionales por consumo excedente que
 * debe pagar el cliente después de adquirir el servicio.
 */
@Entity
@Table(name = "jaapa_tarifas")
@Getter
@Setter
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tar_id")
    private Long id;

    /**
     * Valor que el cliente pagará mensualmente por el servicio básico.
     * Representa el costo fijo mensual independiente del consumo.
     */
    @Column(name = "tar_basica")
    @NotNull(message = "La tarifa básica es obligatoria")
    @Min(value = 1, message = "La tarifa básica debe ser mayor a 0")
    private Double tarifaBasica;

    /**
     * Valor adicional que se cobra al cliente cuando excede el límite máximo de consumo.
     * Se aplica por cada unidad excedente después del límite establecido.
     */
    @Column(name = "tar_adicional")
    @NotNull(message = "La tarifa adicional es obligatoria")
    @Min(value = 0, message = "La tarifa adicional debe ser mayor o igual a 0")
    private Double tarifaAdicional;

    /**
     * Límite máximo de consumo incluido en la tarifa básica.
     * Si el cliente supera este límite, se aplicará la tarifa adicional.
     */
    @Column(name = "tar_limite_maximo_consumo")
    @NotNull(message = "El límite máximo de consumo es obligatorio")
    @Min(value = 0, message = "El límite debe ser mayor o igual a 0")
    private int limiteMaximoConsumo;

    /**
     * Fecha desde la cual esta tarifa entra en vigencia.
     */
    @Column(name = "tar_fecha_inicio")
    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    /**
     * Descripción detallada sobre la tarifa adicional y sus condiciones.
     */
    @Column(name = "tar_descripcion")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @NotBlank(message = "La descripción es obligatorio")
    private String descripcion;
}

