package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jaapa_back.enums.EstadoEnum;
import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, allowGetters = true)
@Entity
@Table(name = "jaapa_consumos")
public class Consumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "con_id")
    private Long id;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "con_fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    //@NotNull(message = "La fecha de corte es obligatoria")
    @Column(name = "con_fecha_corte")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCorte;

    @NotNull(message = "El valor anterior es obligatorio")
    @Column(name = "con_valor_anterior")
    @Min(value = 0, message = "El valor anterior no puede ser negativo")
    private int valorAnterior;

    @NotNull(message = "El valor actual es obligatorio")
    @Column(name = "con_valor_actual")
    @Min(value = 0, message = "El valor actual no puede ser negativo")
    private int valorActual;

    @Column(name = "con_consumo")
    @Min(value = 0, message = "El consumo no puede ser negativo")
    private int consumo;

    @Column(name = "con_costo_adicional")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo adicional no puede ser negativo")
    private BigDecimal costoAdicional;

    @Column(name = "con_total")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total no puede ser negativo")
    private BigDecimal total;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "con_estado", length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoEnum estado;

    @NotNull(message = "El estado de pago es obligatorio")
    @Column(name = "con_estado_pago")
    @Enumerated(EnumType.STRING)
    private EstadoPagoEnum estadoPago;

    @NotNull(message = "La tarifa es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tar_id_fk", referencedColumnName = "tar_id")
    private Tarifa tarifa;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Column(name = "tar_tipo_registro", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoRegistroEnum tipoRegistro;

    @NotNull(message = "El medidor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "med_id_fk", referencedColumnName = "med_id")
    private Medidor medidor;
}
