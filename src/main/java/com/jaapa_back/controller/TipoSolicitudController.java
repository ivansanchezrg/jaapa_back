package com.jaapa_back.controller;

import com.jaapa_back.dto.mapper.TipoSolicitudMapper;
import com.jaapa_back.dto.response.TipoSolicitudResponseDTO;
import com.jaapa_back.model.TipoSolicitud;
import com.jaapa_back.model.Usuario;
import com.jaapa_back.security.constants.SecurityConstants;
import com.jaapa_back.service.TipoSolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-solicitud")
public class TipoSolicitudController {

    private final TipoSolicitudService tipoSolicitudService;
    private final TipoSolicitudMapper tipoSolicitudMapper;

    @Autowired
    public TipoSolicitudController(TipoSolicitudService tipoSolicitudService, TipoSolicitudMapper tipoSolicitudMapper) {
        this.tipoSolicitudService = tipoSolicitudService;
        this.tipoSolicitudMapper = tipoSolicitudMapper;
    }

    /**
     * Obtiene todos los tipos de solicitudes.
     *
     * @return Lista de tipos de solicitudes.
     */
    @GetMapping
    public ResponseEntity<List<TipoSolicitudResponseDTO>> getAllTiposSolicitud() {
        return ResponseEntity.ok(tipoSolicitudMapper.toDtoList(tipoSolicitudService.findAll()));
    }

    /**
     * Endpoint para obtener usuarios por tipo de solicitud.
     *
     * @param nombreTipoSolicitud El nombre del tipo de solicitud a consultar
     * @return ResponseEntity con la lista de usuarios que tienen solicitudes del tipo especificado
     */
    @GetMapping("/usuarios")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<?> getUsuariosPorTipoSolicitud(@RequestParam String nombreTipoSolicitud) {

        List<Usuario> usuarios = tipoSolicitudService.obtenerUsuariosPorTipoSolicitud(nombreTipoSolicitud);

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(usuarios);

    }
}
