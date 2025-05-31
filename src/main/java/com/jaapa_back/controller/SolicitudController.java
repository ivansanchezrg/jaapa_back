package com.jaapa_back.controller;

import com.jaapa_back.dto.SolicitudDetalleDTO;
import com.jaapa_back.dto.request.SolicitudRequestDTO;
import com.jaapa_back.dto.response.AsistenciaListaResponseDTO;
import com.jaapa_back.dto.response.PageResponse;
import com.jaapa_back.dto.response.RequestSummary;
import com.jaapa_back.model.Solicitud;
import com.jaapa_back.projection.SolicitudProjection;
import com.jaapa_back.projection.SolicitudSummaryProjection;
import com.jaapa_back.security.constants.SecurityConstants;
import com.jaapa_back.service.SolicitudService;
import com.jaapa_back.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;


    /**
     * Crea una nueva solicitud.
     *
     * @param solicitudRequestDTO Datos de la nueva solicitud.
     * @return Solicitud creada.
     */
    @PostMapping
    public ResponseEntity<Solicitud> createSolicitud(@RequestBody SolicitudRequestDTO solicitudRequestDTO) {
        Solicitud savedSolicitud = solicitudService.save(solicitudRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedSolicitud);
    }

    /**
     * Endpoint para obtener listado paginado de solicitudes.
     *
     * @param pageable Configuración de paginación
     * @return ResponseEntity con Page<SolicitudProjection>
     */
    @GetMapping("/paginar")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<PageResponse<SolicitudProjection>> listarSolicitudesPaginadas(
            Pageable pageable,
            @RequestParam(required = false) String fechaFrom,
            @RequestParam(required = false) String fechaTo,
            @RequestParam(required = false) String fechaEquals,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String estadoContains,
            @RequestParam(required = false) String numeroSolicitud,
            @RequestParam(required = false) String cedulaUsuario) {

        Page<SolicitudProjection> solicitudes = solicitudService.obtenerSolicitudesPaginadas(
                pageable, estado, estadoContains, fechaFrom, fechaTo, fechaEquals, numeroSolicitud, cedulaUsuario);

        return ResponseEntity.ok(PageUtils.toPageResponse(solicitudes));
    }

    /**
     * Obtiene el resumen estadístico de todas las solicitudes
     * @return Resumen con totales y contadores por estado
     */
    @GetMapping("/resumen")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<SolicitudSummaryProjection> obtenerResumenSolicitudes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String estadoContains,
            @RequestParam(required = false) String fechaFrom,
            @RequestParam(required = false) String fechaTo,
            @RequestParam(required = false) String fechaEquals,
            @RequestParam(required = false) String numeroSolicitud,
            @RequestParam(required = false) String cedulaUsuario
    ) {
        SolicitudSummaryProjection resumen = solicitudService.obtenerResumenSolicitudes(
                estado, estadoContains, fechaFrom, fechaTo, fechaEquals, numeroSolicitud, cedulaUsuario);
        return ResponseEntity.ok(resumen);
    }

    /**
     * API para obtener el detalle completo de una solicitud
     *
     * @param id Identificador de la solicitud
     * @return Información detallada de la solicitud
     */
    @GetMapping("/detalle/{id}")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<SolicitudDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        SolicitudDetalleDTO detalle = solicitudService.obtenerDetalleSolicitud(id);
        return ResponseEntity.ok(detalle);
    }

    /**
     * Actualiza la URL del certificado de instalación de una solicitud
     * @param request mapa con numeroSolicitud y urlCertificadoInstalacion
     * @return ResponseEntity con estado 200 si se actualizó, 404 si no existe
     */
    @PatchMapping("/actualizar-certificado")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<?> actualizarCertificado(@RequestBody Map<String, String> request) {
        String numeroSolicitud = request.get("numeroSolicitud");
        String urlCertificado = request.get("urlCertificadoInstalacion");

        boolean actualizada = solicitudService.actualizarCertificado(numeroSolicitud, urlCertificado);

        return actualizada ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
