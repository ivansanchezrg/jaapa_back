package com.jaapa_back.projection;

import java.math.BigDecimal;

public interface SolicitudSummaryProjection {
    Long getTotalRequests();
    BigDecimal getTotalCollectedAmount();
    BigDecimal getPendingAmount();
    BigDecimal getTotalAmount();
    Long getAprobadas();
    Long getRechazadas();
    Long getEnProceso();
    Long getCompletadas();
}
