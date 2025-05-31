package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaapa_back.annotations.ToUpperCase;
import com.jaapa_back.annotations.UpperCaseListener;
import com.jaapa_back.enums.EstadoEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "jaapa_usuarios")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(UpperCaseListener.class)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    private Long id;

    @NotNull(message = "La cédula es obligatoria")
    @NotBlank(message = "La cédula es obligatoria.")
    @Column(name = "usu_cedula", length = 10,nullable = false, unique = true)
    @Size(min = 10, max = 10, message = "La cédula debe tener 10 caracteres.")
    @Pattern(regexp = "^[0-9]+$", message = "La cédula debe contener solo números.")
    private String cedula;

    @NotNull(message = "El nombre es obligatorio")
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "usu_nombre", length = 100,nullable = false)
    @Size(max = 100, message = "El nombre debe tener maximo 100 caracteres.")
    @ToUpperCase
    private String nombre;

    @NotNull(message = "El apellido es obligatorio")
    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "usu_apellido", length = 100,nullable = false)
    @Size(max = 100, message = "El apellido debe tener maximo 100 caracteres.")
    @ToUpperCase
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Column(name = "usu_fecha_nacimiento", length = 50,nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate fechaNacimiento;

    @Column(name = "usu_telefono", length = 10)
    @Size(max = 9, message = "El teléfono no debe exceder los 9 caracteres.")
    private String telefono;

    @Column(name = "usu_celular", length = 10)
    @NotNull(message = "El celular es obligatoria.")
    @NotBlank(message = "El celular es obligatoria.")
    @Size(min = 10, max = 10, message = "El celular debe tener 10 números.")
    @Pattern(regexp = "^[0-9]+$", message = "El celular debe contener solo números.")
    private String celular;

    @NotNull(message = "El correo electrónico es obligatoria.")
    @NotBlank(message = "El correo electrónico es obligatoria.")
    @Column(name = "usu_correo", length = 50)
    @Email(message = "El correo electrónico no es válido.")
    private String correo;

    @NotNull(message = "El estado es obligatorio.")
    @Column(name = "usu_estado",  length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoEnum estado;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Column(name = "usu_tipo_registro", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoRegistroEnum tipoRegistro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Medidor> medidores;

}