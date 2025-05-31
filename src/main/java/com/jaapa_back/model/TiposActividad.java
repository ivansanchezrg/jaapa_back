package com.jaapa_back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "jaapa_tipos_actividad")
@Getter
@Setter
public class TiposActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tip_id;

    @Column(name = "tip_nombre", nullable = false)
    private String nombre;

    @Column(name = "tip_descripcion")
    private String descripcion;

    @Column(name = "tip_valor", nullable = false)
    private Double valor;

    @Column(name = "tip_fecha_creacion")
    private LocalDate fecha_creacion;

    @Column(name = "tip_usuario_modificacion")
    private String usuarioModificacion;
}
