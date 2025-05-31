package com.jaapa_back.model;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Correo {

    private Usuario usuario;
    private String pdf;
    private String numeroSolicitud;
    private String tipoSolicitud;
}
