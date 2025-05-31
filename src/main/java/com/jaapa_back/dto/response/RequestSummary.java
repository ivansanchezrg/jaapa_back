package com.jaapa_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSummary {
    private Long totalRequests;
    private BigDecimal totalPaidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal totalAmount;
    private Map<String, Long> requestsByStatus;
}

