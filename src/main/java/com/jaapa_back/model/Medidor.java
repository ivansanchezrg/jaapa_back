package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jaapa_back.annotations.ToUpperCase;
import com.jaapa_back.annotations.UpperCaseListener;
import com.jaapa_back.enums.EstadoEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(UpperCaseListener.class)
@Entity
@Table(name = "jaapa_medidores")
public class Medidor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "med_id")
    private Long id;

    @NotNull(message = "El código es obligatorio")
    @NotBlank(message = "El código es obligatorio")
    @Column(name = "med_codigo", nullable = false, length = 100)
    @Size(max = 100, message = "El código debe tener maximo 100 caracteres")
    @ToUpperCase
    private String codigo;

    @NotNull(message = "La marca es obligatorio")
    @NotBlank(message = "La marca es obligatorio")
    @Column(name = "med_marca", length = 100)
    @Size(max = 100, message = "La marca no debe exceder los 100 caracteres")
    @ToUpperCase
    private String marca;

    @NotNull(message = "El modelo es obligatorio")
    @NotBlank(message = "El modelo es obligatorio")
    @Column(name = "med_modelo", length = 100)
    @Size(max = 100, message = "El modelo no debe exceder los 100 caracteres")
    @ToUpperCase
    private String modelo;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "med_estado", nullable = false , length = 20)
    private EstadoEnum estado;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Column(name = "med_tipo_registro", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoRegistroEnum tipoRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @OneToOne(optional = true)
    @JsonIgnore
    private Solicitud solicitud; // Relacion opcional , ya que luego se asocia a un medidor

    @OneToMany(mappedBy = "medidor", cascade = CascadeType.ALL)
    private List<Consumo> consumos;

}