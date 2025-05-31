package com.jaapa_back.controller;


import com.jaapa_back.model.TiposActividad;
import com.jaapa_back.security.constants.SecurityConstants;
import com.jaapa_back.service.TiposMultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipos-multa")
public class TiposMultaController {
    private final TiposMultaService tipoService;

    @Autowired
    public TiposMultaController(TiposMultaService tipoService) {
        this.tipoService = tipoService;
    }


    /**
     * Obtiene todos los tipos de multas.
     *
     * @return Lista de tipos de multas.
     */
    @GetMapping
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<Iterable<TiposActividad>> getAllTipos() {
        return ResponseEntity.ok(tipoService.findAll());
    }
}
