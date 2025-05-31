package com.jaapa_back.dto;

import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.TipoRegistroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private String cedula;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String celular;
    private String telefono;
    private String correo;
    private String estado;
    private String tipoRegistro;
}
