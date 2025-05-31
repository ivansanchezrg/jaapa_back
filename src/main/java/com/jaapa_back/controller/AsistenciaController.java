package com.jaapa_back.controller;

import com.jaapa_back.dto.AsistenciaDTO;
import com.jaapa_back.dto.response.ApiResponse;
import com.jaapa_back.dto.response.AsistenciaDetalleUsuarioDTO;
import com.jaapa_back.dto.response.AsistenciaListaResponseDTO;
import com.jaapa_back.dto.response.AsistenciaResumenDTO;
import com.jaapa_back.security.constants.SecurityConstants;
import com.jaapa_back.service.AsistenciaDetalleService;
import com.jaapa_back.service.AsistenciaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/asistencia")
public class AsistenciaController {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaController.class);

    @Autowired
    private AsistenciaService asistenciaService;
    @Autowired
    private AsistenciaDetalleService asistenciaDetalleService;

    /**
     * Endpoint para registrar una nueva asistencia
     *
     * @param asistenciaDTO Datos de la asistencia y asistentes
     * @return ResponseEntity con mensaje de confirmación
     */
    @PostMapping
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<ApiResponse> registrarAsistencia(@Valid @RequestBody AsistenciaDTO asistenciaDTO) {
        asistenciaService.guardarAsistencia(asistenciaDTO);

        ApiResponse response = new ApiResponse(
                HttpStatus.CREATED,
                "Asistencia registrada correctamente6565",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Obtiene los detalles de asistencia con información de usuarios.
     *
     * @param asistenciaId ID de la asistencia
     * @return Lista de detalles con datos de usuario asociados
     */
    @GetMapping("/{asistenciaId}/detalles")
    public ResponseEntity<List<AsistenciaDetalleUsuarioDTO>> obtenerDetallesAsistencia(@PathVariable Long asistenciaId) {
        List<AsistenciaDetalleUsuarioDTO> detalles = asistenciaDetalleService.obtenerDetallesUsuariosPorAsistencia(asistenciaId);
        return ResponseEntity.ok(detalles);
    }

    /**
     * Endpoint para listar asistencias con paginación y filtrado por fechas opcional.
     *
     * @param pageable Configuración de paginación
     * @param fechaFrom Fecha inicial para filtrado (opcional)
     * @param fechaTo Fecha final para filtrado (opcional)
     * @param fechaEquals Fecha exacta para filtrado (opcional)
     * @return ResponseEntity con la página de DTOs de asistencias
     */
    @GetMapping("/paginar")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<Page<AsistenciaListaResponseDTO>> listarAsistenciasPaginadas(
            Pageable pageable,
            @RequestParam(required = false) String fechaFrom,
            @RequestParam(required = false) String fechaTo,
            @RequestParam(required = false) String fechaEquals,
            @RequestParam(required = false) String fechaNotEqual,
            @RequestParam(required = false) Boolean fechaIsBlank,
            @RequestParam(required = false) Boolean fechaIsNotBlank) {


//        log.info("Pageable recibido: page={}, size={}, sort={}",
//                pageable.getPageNumber(),
//                pageable.getPageSize(),
//                pageable.getSort());

        Page<AsistenciaListaResponseDTO> asistencias = asistenciaService.listarAsistenciasPaginadas(
                pageable, fechaFrom, fechaTo, fechaEquals, fechaNotEqual, fechaIsBlank, fechaIsNotBlank);
        return ResponseEntity.ok(asistencias);
    }

    /**
     * Endpoint para obtener resúmenes de asistencias con filtros opcionales por fecha.
     *
     * @param fechaFrom Fecha inicio opcional en formato String (yyyy-MM-dd)
     * @param fechaTo Fecha fin opcional en formato String (yyyy-MM-dd)
     * @param fechaEquals Fecha específica opcional en formato String (yyyy-MM-dd)
     * @return ResponseEntity con AsistenciaResumenDTO que contiene conteos y montos monetarios
     */
    @GetMapping("/resumen")
    @PreAuthorize(SecurityConstants.HAS_SUPER_ADMIN_OR_ADMIN)
    public ResponseEntity<AsistenciaResumenDTO> obtenerResumenAsistencias(
            @RequestParam(required = false) String fechaFrom,
            @RequestParam(required = false) String fechaTo,
            @RequestParam(required = false) String fechaEquals,
            @RequestParam(required = false) String fechaNotEqual,
            @RequestParam(required = false) Boolean fechaIsBlank,
            @RequestParam(required = false) Boolean fechaIsNotBlank) {

        AsistenciaResumenDTO resumen = asistenciaService.obtenerResumenAsistencias(
                fechaFrom, fechaTo, fechaEquals, fechaNotEqual, fechaIsBlank, fechaIsNotBlank);
        return ResponseEntity.ok(resumen);
    }

}
