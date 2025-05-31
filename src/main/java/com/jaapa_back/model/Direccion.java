package com.jaapa_back.model;

import com.jaapa_back.annotations.ToUpperCase;
import com.jaapa_back.annotations.UpperCaseListener;
import com.jaapa_back.enums.TipoRegistroEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "jaapa_direcciones")
@EntityListeners(UpperCaseListener.class)
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dir_id")
    private Long id;

    @Column(name = "dir_calle_principal", length = 200)
    @Size(max = 200, message = "La calle principal no debe exceder los 200 caracteres.")
    @ToUpperCase
    @NotNull(message = "La calle principal es obligatoria.")
    @NotBlank(message = "La calle principal es obligatoria.")
    private String callePrincipal;

    @Column(name = "dir_calle_secundaria", length = 200)
    @Size(max = 200, message = "La calle secundaria no debe exceder los 200 caracteres.")
    @ToUpperCase
    @NotNull(message = "La calle secundaria es obligatoria.")
    @NotBlank(message = "La calle secundaria es obligatoria.")
    private String calleSecundaria;

    @NotNull(message = "La referencia es obligatoria")
    @NotBlank(message = "La referencia es obligatoria")
    @Column(name = "dir_referencia", length = 200,nullable = false)
    @Size(max = 200, message = "La referencia debe tener maximo 200 caracteres.")
    @ToUpperCase
    private String referencia;

    @NotNull(message = "El barrio es obligatorio")
    @NotBlank(message = "El barrio es obligatorio")
    @Column(name = "dir_barrio", length = 100,nullable = false)
    @Size(max = 100, message = "El barrio debe tener maximo 100 caracteres.")
    @ToUpperCase
    private String barrio;

    @NotNull(message = "El tipo de registro es obligatorio")
    @Column(name = "dir_tipo_registro", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoRegistroEnum tipoRegistro;
}
