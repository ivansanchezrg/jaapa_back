package com.jaapa_back.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "jaapa_usuarios_sistema")
public class UsuarioSistema implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_sis_id")
    private Long id;

    @Column(length = 50,  name = "usu_sis_username", unique = true)
    private String username;

    @Column(length = 60, name = "usu_sis_password")
    private String password;

    @Column(columnDefinition = "boolean")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "usuarios_sistema_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "role_id"})
    )
    private Set<Role> roles;
}
