package com.jaapa_back.service;

import com.jaapa_back.dto.DireccionDTO;
import com.jaapa_back.dto.MedidorDTO;
import com.jaapa_back.dto.SolicitudDetalleDTO;
import com.jaapa_back.dto.UsuarioDTO;
import com.jaapa_back.dto.request.SolicitudRequestDTO;
import com.jaapa_back.dto.response.RequestSummary;
import com.jaapa_back.enums.*;
import com.jaapa_back.exception.custom.DocumentoException;
import com.jaapa_back.exception.custom.EmailServiceException;
import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import com.jaapa_back.model.*;
import com.jaapa_back.projection.SolicitudProjection;
import com.jaapa_back.projection.SolicitudSummaryProjection;
import com.jaapa_back.projection.SolicitudSummaryProjectionImpl;
import com.jaapa_back.repository.SolicitudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final VerificarCedulaService verificarCedulaService;
    private final UsuarioService usuarioService;
    private final TipoSolicitudService tipoSolicitudService;
    private final DireccionService direccionService;
    private final PagoDiferidosService pagoDiferidosService;
    private final PdfService pdfService;
    private final EmailService emailService;
    private final AgGridService agGridService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SolicitudService(SolicitudRepository solicitudRepository, VerificarCedulaService verificarCedulaService,
                            UsuarioService usuarioService, TipoSolicitudService tipoSolicitudService, DireccionService direccionService,
                            PagoDiferidosService pagoDiferidosService, PdfService pdfService, EmailService emailService,
                            AgGridService agGridService) {
        this.solicitudRepository = solicitudRepository;
        this.verificarCedulaService = verificarCedulaService;
        this.usuarioService = usuarioService;
        this.tipoSolicitudService = tipoSolicitudService;
        this.direccionService = direccionService;
        this.pagoDiferidosService = pagoDiferidosService;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.agGridService = agGridService;
    }

    /**
     * Guarda una nueva solicitud en el sistema.
     *
     * @param solicitudDTO El DTO que contiene los datos de la solicitud a guardar.
     * @return La solicitud guardada.
     */
    @Transactional
    public Solicitud save(SolicitudRequestDTO solicitudDTO) {
        // Validaciones iniciales
        this.validarSolicitudDTO(solicitudDTO);

        // Obtener datos necesarios
        Usuario usuario = obtenerOCrearUsuario(solicitudDTO.getUsuario());
        Direccion direccion = crearDireccion(solicitudDTO.getDireccion());
        TipoSolicitud tipoSolicitud = tipoSolicitudService.getTipoSolicitudByNombre(solicitudDTO.getTipoSolicitud().getNombre());

        // Crear medidor solo para solicitudes de AGUA
        Medidor medidor = null;
        if (TipoTransaccionEnum.AGUA.name().equals(tipoSolicitud.getNombre())) {
            if (solicitudDTO.getMedidor() == null) {
                throw new ValorActualIncorrectoException("El medidor es obligatorio para solicitudes de AGUA");
            }
            medidor = crearMedidor(solicitudDTO.getMedidor());
        }

        Solicitud solicitud = null;
        String tipo = solicitudDTO.getTipoPago().name().toUpperCase();

        // Procesar la solicitud según el tipo
        if (TipoPagoSolicitudEnum.DIFERIDO.name().equals(tipo) && TipoTransaccionEnum.AGUA.name().equals(tipoSolicitud.getNombre())) {
            solicitud = procesarSolicitudDiferida(solicitudDTO, usuario, direccion, tipoSolicitud);
        } else if (TipoPagoSolicitudEnum.TOTAL.name().equals(tipo) && (tipoSolicitud.getNombre().equals(TipoTransaccionEnum.ALCANTARILLADO.name()) ||
                tipoSolicitud.getNombre().equals(TipoTransaccionEnum.AGUA.name()))) {
            solicitud = procesarSolicitudTotal(solicitudDTO, usuario, direccion, tipoSolicitud);
        } else {
            throw new ValorActualIncorrectoException("Solo para el servicio de AGUA puede ser el pago DIFERIDO");
        }

        // Asociar medidor a la solicitud si existe
        if (medidor != null) {
            solicitud.setMedidor(medidor);
            medidor.setSolicitud(solicitud);
        }

        // Refrescar la entidad para obtener el número de solicitud generado en la base de datos posgrest.
        entityManager.flush();
        entityManager.refresh(solicitud);

        if (solicitud.getNumeroSolicitud() == null || solicitud.getNumeroSolicitud().trim().isEmpty()) {
            throw new ValorActualIncorrectoException("Número de solicitud no encontrado.");
        }
        return solicitud;
    }

    /**
     * Valida los datos de una solicitud DTO.
     *
     * @param solicitudDTO El DTO de la solicitud a validar.
     * @throws ValorActualIncorrectoException Si alguno de los campos requeridos es nulo o inválido.
     */
    private void validarSolicitudDTO(SolicitudRequestDTO solicitudDTO) throws ValorActualIncorrectoException {
        if (solicitudDTO == null) {
            throw new ValorActualIncorrectoException("La solicitud no puede ser nula");
        }
        if (solicitudDTO.getTipoSolicitud().getNombre() == null) {
            throw new ValorActualIncorrectoException("El id del Tipo de Solicitud no puede ser nulo");
        }
        if (solicitudDTO.getUsuario() == null || solicitudDTO.getUsuario().getCedula() == null) {
            throw new ValorActualIncorrectoException("El usuario y la cédula no pueden ser nulos");
        }
        if (solicitudDTO.getTipoPago() == null) {
            throw new ValorActualIncorrectoException("El tipo de pago no puede ser nulo");
        }

        // No hace falta propagar una expeccion ya que el metodo "verificarCedula2" ya lo hace.
        String cedula = solicitudDTO.getUsuario().getCedula();
        verificarCedulaService.verificarCedula(cedula);
    }

    /**
     * Obtiene un usuario existente o crea uno nuevo basado en los datos proporcionados.
     *
     * @param usuarioDTO DTO con los datos del usuario.
     * @return El usuario obtenido o creado.
     * @throws ValorActualIncorrectoException Si el rol asignado no es el esperado.
     */
    private Usuario obtenerOCrearUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioService.findByCedula(usuarioDTO.getCedula());

        if (usuario != null) {
            // Usuario existente, lo retornamos sin modificar
            usuario.setTipoRegistro(TipoRegistroEnum.WEB);
            return usuario;
        }

        // Usuario no existe, creamos uno nuevo
        usuario = new Usuario();
        usuario.setEstado(EstadoEnum.ACTIVO);
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setCedula(usuarioDTO.getCedula());
        usuario.setCelular(usuarioDTO.getCelular());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setTipoRegistro(TipoRegistroEnum.WEB);

        return usuario;
    }

    /**
     * Crea una nueva entidad Direccion basada en los datos del DTO.
     *
     * @param direccionDTO DTO con los datos de la dirección.
     * @return La nueva entidad Direccion creada.
     */
    private Direccion crearDireccion(DireccionDTO direccionDTO) {
        //Crear la dirección
        Direccion direccion = null;

        direccion = new Direccion();
        direccion.setBarrio(direccionDTO.getBarrio());
        direccion.setReferencia(direccionDTO.getReferencia());
        direccion.setCallePrincipal(direccionDTO.getCallePrincipal());
        direccion.setCalleSecundaria(direccionDTO.getCalleSecundaria());
        direccion.setTipoRegistro(TipoRegistroEnum.WEB);

        return direccion;
    }

    private Medidor crearMedidor(MedidorDTO medidorDTO) {
        Medidor medidor = new Medidor();
        medidor.setCodigo(medidorDTO.getCodigo());
        medidor.setMarca(medidorDTO.getMarca());
        medidor.setModelo(medidorDTO.getModelo());
        medidor.setEstado(EstadoEnum.ACTIVO);
        medidor.setTipoRegistro(TipoRegistroEnum.WEB);

        return medidor;
    }

    private Solicitud procesarSolicitudDiferida(SolicitudRequestDTO solicitudDTO, Usuario usuario,
                                                Direccion direccion, TipoSolicitud tipoSolicitud) {
        // Guardar usuario y dirección
        usuario = usuarioService.save(usuario);
        direccion = direccionService.save(direccion);

        // Crear solicitud
        Solicitud solicitud = crearSolicitud(solicitudDTO, usuario, direccion, tipoSolicitud);

        // Guardar la solicitud
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        // Configurar pagos diferidos
        pagoDiferidosService.configurarPagosDiferidos(solicitudGuardada, tipoSolicitud.getId());

        return solicitudGuardada;
    }

    /**
     * Crea una nueva entidad Solicitud basada en los datos proporcionados.
     *
     * @param solicitudDTO DTO con los datos de la solicitud.
     * @param usuario El usuario asociado a la solicitud.
     * @param direccion La dirección asociada a la solicitud.
     * @param tipoSolicitud El tipo de solicitud.
     * @return La nueva entidad Solicitud creada.
     */
    private Solicitud crearSolicitud(SolicitudRequestDTO solicitudDTO, Usuario usuario, Direccion direccion, TipoSolicitud tipoSolicitud) {
        Solicitud solicitud = null;

        solicitud = new Solicitud();
        solicitud.setTipoPago(solicitudDTO.getTipoPago());
        solicitud.setFecha(LocalDate.now());
        solicitud.setEstado(EstadoSolicitudEnum.EN_PROCESO);
        solicitud.setEstadoPago(EstadoPagoEnum.PENDIENTE);
        solicitud.setMontoPagado(solicitudDTO.getMontoPagado());
        solicitud.setMontoPendiente(BigDecimal.valueOf(tipoSolicitud.getCosto()));
        solicitud.setMontoTotal(BigDecimal.valueOf(tipoSolicitud.getCosto()));
        solicitud.setUsuario(usuario);
        solicitud.setTipoSolicitud(tipoSolicitud);
        solicitud.setDireccion(direccion);
        solicitud.setTipoRegistro(TipoRegistroEnum.WEB);
        return solicitud;
    }

    private Solicitud procesarSolicitudTotal(SolicitudRequestDTO solicitudDTO, Usuario usuario,
                                             Direccion direccion, TipoSolicitud tipoSolicitud) {
        // Guardar usuario y dirección
        usuario = usuarioService.save(usuario);
        direccion = direccionService.save(direccion);

        // Crear solicitud
        Solicitud solicitud = crearSolicitud(solicitudDTO, usuario, direccion, tipoSolicitud);

        // Guardar y refrescar la solicitud para obtener el número generado
        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);
        entityManager.flush();
        entityManager.refresh(solicitudGuardada);

        return solicitudGuardada;
    }

    private Correo creatCorreo(Solicitud solicitud, byte[] pdfBytes){
        Correo correo = new Correo();
        correo.setUsuario(solicitud.getUsuario());
        correo.setNumeroSolicitud(solicitud.getNumeroSolicitud());
        correo.setTipoSolicitud(solicitud.getTipoSolicitud().getNombre());
        correo.setPdf(Base64.getEncoder().encodeToString(pdfBytes));


        return correo;
    }

    /**
     * Obtiene las solicitudes paginadas con filtros opcionales de estado y fecha
     *
     * @param pageable configuración de paginación
     * @param estado valor exacto del estado a filtrar
     * @param estadoContains texto parcial del estado a buscar
     * @param fechaFrom fecha inicial para rango o búsqueda posterior
     * @param fechaTo fecha final para rango o búsqueda anterior
     * @param fechaEquals fecha exacta a buscar
     * @return página de solicitudes filtradas
     */
    @Transactional(readOnly = true)
    public Page<SolicitudProjection> obtenerSolicitudesPaginadas(
            Pageable pageable,
            String estado,
            String estadoContains,
            String fechaFrom,
            String fechaTo,
            String fechaEquals,
            String numeroSolicitud,
            String cedulaUsuario) {

        pageable = this.agGridService.aplicarOrdenamientoDefault(pageable);

        // Filtrado por estado
        if (estado != null || estadoContains != null) {
            return filtrarPorEstado(pageable, estado, estadoContains);
        }

        // Filtrado por fecha
        if (fechaEquals != null || fechaFrom != null || fechaTo != null) {
            return filtrarPorFecha(pageable, fechaEquals, fechaFrom, fechaTo);
        }

        // Filtrado por numeroSolicitud
        if (numeroSolicitud != null) {
            return solicitudRepository.findAllProjectedByNumeroSolicitud(pageable, numeroSolicitud);
        }

        // Filtrado por numero de Cedula
        if (cedulaUsuario != null) {
            //System.out.println("ENTRERREEE---->>>"  + cedulaUsuario);
            return solicitudRepository.findAllProjectedByCedulaUsuario(pageable, cedulaUsuario);
        }

        // retorna la lista normal sin filtros
        return solicitudRepository.findAllProjectedPaginated(pageable);
    }

    /**
     * Filtra las solicitudes por estado
     *
     * @param pageable configuración de paginación
     * @param estado valor exacto del estado (se convierte a enum)
     * @param estadoContains texto parcial para búsqueda de estado
     * @return página de solicitudes filtradas por estado
     */
    private Page<SolicitudProjection> filtrarPorEstado(Pageable pageable, String estado, String estadoContains) {
        if (estado != null) {
            try {
                EstadoSolicitudEnum estadoEnum = EstadoSolicitudEnum.valueOf(estado.toUpperCase());
                return solicitudRepository.findAllProjectedByEstado(pageable, estadoEnum);
            } catch (IllegalArgumentException e) {
                return Page.empty(pageable);
            }
        }
        return solicitudRepository.findAllProjectedByEstadoContains(pageable, estadoContains);
    }

    /**
     * Filtra las solicitudes por fecha según diferentes criterios
     *
     * @param pageable configuración de paginación
     * @param fechaEquals fecha exacta para igualdad
     * @param fechaFrom fecha inicial para rango o mayor que
     * @param fechaTo fecha final para rango o menor que
     * @return página de solicitudes filtradas por fecha
     */
    private Page<SolicitudProjection> filtrarPorFecha(
            Pageable pageable, String fechaEquals, String fechaFrom, String fechaTo) {
        if (fechaEquals != null) {
            LocalDate fecha = agGridService.parseFecha(fechaEquals);
            return solicitudRepository.findAllProjectedByFechaEquals(pageable, fecha);
        } else if (fechaFrom != null && fechaTo != null) {
            LocalDate from = agGridService.parseFecha(fechaFrom);
            LocalDate to = agGridService.parseFecha(fechaTo);
            return solicitudRepository.findAllProjectedByFechaBetween(pageable, from, to);
        } else if (fechaFrom != null) {
            LocalDate from = agGridService.parseFecha(fechaFrom);
            return solicitudRepository.findAllProjectedByFechaAfter(pageable, from);
        } else {
            LocalDate to = agGridService.parseFecha(fechaTo);
            return solicitudRepository.findAllProjectedByFechaBefore(pageable, to);
        }
    }


    /* ==============================================================================
    * METODOS PARA EL RESUMEN
    * =============================================================================== */

    @Transactional(readOnly = true)
    public SolicitudSummaryProjection obtenerResumenSolicitudes(
            String estado, String estadoContains,
            String fechaFrom, String fechaTo, String fechaEquals, String numeroSolicitud,String cedulaUsuario) {

        // Filtrado por estado
        if (estado != null || estadoContains != null) {
            return filtrarResumenPorEstado(estado, estadoContains);
        }

        // Filtrado por fecha
        if (fechaEquals != null || fechaFrom != null || fechaTo != null) {
            return filtrarResumenPorFecha(fechaEquals, fechaFrom, fechaTo);
        }

        // Filtrado por numeroSolicitud
        if (numeroSolicitud != null) {
            return solicitudRepository.findSummaryByNumeroSolicitud(numeroSolicitud);
        }

        // Filtrado por Cedula
        if (cedulaUsuario != null) {
            return solicitudRepository.findSummaryByCedulaUsuario(cedulaUsuario);
        }

        return solicitudRepository.findSummary();
    }

    private SolicitudSummaryProjection filtrarResumenPorEstado(String estado, String estadoContains) {
        if (estado != null) {
            try {
                EstadoSolicitudEnum estadoEnum = EstadoSolicitudEnum.valueOf(estado.toUpperCase());
                return solicitudRepository.findSummaryByEstado(estadoEnum);
            } catch (IllegalArgumentException e) {
                return new SolicitudSummaryProjectionImpl(0L, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, 0L, 0L, 0L, 0L);
            }
        }
        return solicitudRepository.findSummaryByEstadoContains(estadoContains);
    }

    private SolicitudSummaryProjection filtrarResumenPorFecha(
            String fechaEquals, String fechaFrom, String fechaTo) {
        if (fechaEquals != null) {
            LocalDate fecha = agGridService.parseFecha(fechaEquals);
            return solicitudRepository.findSummaryByFechaEquals(fecha);
        } else if (fechaFrom != null && fechaTo != null) {
            LocalDate from = agGridService.parseFecha(fechaFrom);
            LocalDate to = agGridService.parseFecha(fechaTo);
            return solicitudRepository.findSummaryByFechaBetween(from, to);
        } else if (fechaFrom != null) {
            LocalDate from = agGridService.parseFecha(fechaFrom);
            return solicitudRepository.findSummaryByFechaAfter(from);
        } else {
            LocalDate to = agGridService.parseFecha(fechaTo);
            return solicitudRepository.findSummaryByFechaBefore(to);
        }
    }

    /* ==============================================================================
     * METODOS PARA EL DETALLE DE UNA SOLICITUD
     * =============================================================================== */
    /**
     * Obtiene la información detallada de una solicitud específica.
     * -
     * Este método recupera datos completos del usuario asociado, la dirección
     * y el medidor vinculados a una solicitud determinada.
     *
     * @param solicitudId El identificador único de la solicitud
     * @return Un objeto DTO con la información detallada de la solicitud
     * @throws EntityNotFoundException Si no se encuentra la solicitud con el ID proporcionado
     */
    public SolicitudDetalleDTO obtenerDetalleSolicitud(Long solicitudId) {
        SolicitudDetalleDTO detalle = solicitudRepository.findSolicitudDetalleCompletoById(solicitudId);
        if (detalle == null) {
            throw new EntityNotFoundException("Solicitud detalle no encontrada con ID: " + solicitudId);
        }
        return detalle;
    }

    /* ==============================================================================
     * METODO CAMBIAR DE ESTADO DE "PAGADA" A "COMPLETADA" Y GUARDAR LA URL DEL CERTIFICADO
     * =============================================================================== */
    /**
     * Actualiza la URL del certificado de instalación de una solicitud
     * @param numeroSolicitud número de solicitud a actualizar
     * @param urlCertificado URL del certificado subido a Firebase Storage
     * @return true si se actualizó correctamente, false si no se encontró la solicitud
     */
    public boolean actualizarCertificado(String numeroSolicitud, String urlCertificado) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findByNumeroSolicitud(numeroSolicitud);

        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();
            solicitud.setUrlCertificadoInstalacion(urlCertificado);
            solicitud.setEstado(EstadoSolicitudEnum.COMPLETADA);
            solicitudRepository.save(solicitud);
            return true;
        }

        return false;
    }


}


