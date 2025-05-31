package com.jaapa_back.projection;

import java.math.BigDecimal;

public class SolicitudSummaryProjectionImpl implements SolicitudSummaryProjection  {

    private Long totalRequests;
    private BigDecimal totalCollectedAmount;
    private BigDecimal pendingAmount;
    private BigDecimal totalAmount;
    private Long aprobadas;
    private Long rechazadas;
    private Long enProceso;
    private Long completadas;

    public SolicitudSummaryProjectionImpl(Long totalRequests,
                                          BigDecimal totalCollectedAmount,
                                          BigDecimal pendingAmount,
                                          BigDecimal totalAmount,
                                          Long aprobadas,
                                          Long rechazadas,
                                          Long enProceso,
                                          Long completadas) {
        this.totalRequests = totalRequests;
        this.totalCollectedAmount = totalCollectedAmount;
        this.pendingAmount = pendingAmount;
        this.totalAmount = totalAmount;
        this.aprobadas = aprobadas;
        this.rechazadas = rechazadas;
        this.enProceso = enProceso;
        this.completadas = completadas;
    }

    @Override
    public Long getTotalRequests() {
        return totalRequests;
    }

    @Override
    public BigDecimal getTotalCollectedAmount() {
        return totalCollectedAmount;
    }

    @Override
    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    @Override
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public Long getAprobadas() {
        return aprobadas;
    }

    @Override
    public Long getRechazadas() {
        return rechazadas;
    }

    @Override
    public Long getEnProceso() {
        return enProceso;
    }

    @Override
    public Long getCompletadas() {
        return completadas;
    }
}
