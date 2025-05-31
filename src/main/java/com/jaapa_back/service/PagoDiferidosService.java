package com.jaapa_back.service;

import com.jaapa_back.enums.EstadoPagoEnum;
import com.jaapa_back.enums.TipoPagoSolicitudEnum;
import com.jaapa_back.exception.custom.EntityNotFoundException;
import com.jaapa_back.model.PagoDiferidoSolicitud;
import com.jaapa_back.model.Solicitud;
import com.jaapa_back.model.TipoSolicitud;
import com.jaapa_back.repository.PagoDiferidosRepository;
import com.jaapa_back.repository.TipoSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PagoDiferidosService {

    @Autowired
    private PagoDiferidosRepository pagoDiferidosRepository;
    @Autowired
    private TipoSolicitudService tipoSolicitudService;


    /**
     * Configura los pagos diferidos en tres cuotas mensuales.
     *
     * @param solicitud El ID de la solicitud para la que se configuran los pagos..
     */
    public void configurarPagosDiferidos(Solicitud solicitud, Long idTipoSolicitud) throws EntityNotFoundException {
        TipoSolicitud findTipoSolicitud = tipoSolicitudService.findById(idTipoSolicitud);
        Double cuotaDiferidas = (findTipoSolicitud.getCosto() - findTipoSolicitud.getValorDiferidoInicial()) / 3;
        LocalDate fechaPago = LocalDate.now();

        PagoDiferidoSolicitud pagoInicial = new PagoDiferidoSolicitud();
        pagoInicial.setFechaPago(fechaPago);
        pagoInicial.setMontoPago(findTipoSolicitud.getValorDiferidoInicial());
        pagoInicial.setTipoPagoSolicitudEnum(TipoPagoSolicitudEnum.DIFERIDO);
        pagoInicial.setEstadoPagoEnum(EstadoPagoEnum.PENDIENTE);
        pagoInicial.setSolicitud(solicitud);
        pagoDiferidosRepository.save(pagoInicial);

        for (int i = 1; i <= 3; i++) {
            PagoDiferidoSolicitud pagoDiferido = new PagoDiferidoSolicitud();
            fechaPago = fechaPago.plusMonths(1);
            pagoDiferido.setFechaPago(fechaPago);
            pagoDiferido.setMontoPago(cuotaDiferidas);
            pagoDiferido.setTipoPagoSolicitudEnum(TipoPagoSolicitudEnum.DIFERIDO);
            pagoDiferido.setEstadoPagoEnum(EstadoPagoEnum.PENDIENTE);
            pagoDiferido.setSolicitud(solicitud);

            pagoDiferidosRepository.save(pagoDiferido);
        }
    }
}
