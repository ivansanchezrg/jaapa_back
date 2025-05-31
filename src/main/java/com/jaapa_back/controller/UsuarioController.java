package com.jaapa_back.controller;

import com.jaapa_back.model.Usuario;
import com.jaapa_back.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    /**
     * Endpoint para buscar un usuario por su cédula
     * @param cedula La cédula del usuario a buscar
     * @return ResponseEntity con el usuario encontrado o 404 si no existe
     */
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Usuario> buscarPorCedula(@PathVariable String cedula){
        Usuario usuario = usuarioService.findByCedula(cedula);
        return ResponseEntity.ok(usuario);
    }
}
