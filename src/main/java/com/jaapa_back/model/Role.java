package com.jaapa_back.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jaapa_back.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "jaapa_roles")
@Getter
@Setter
@NoArgsConstructor // Lombok: genera un constructor sin argumentos
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_nombre", length = 50, unique = true)
    private RoleEnum nombre;

    @Column(name = "rol_descripcion",  length = 200)
    private String descripcion;
}
