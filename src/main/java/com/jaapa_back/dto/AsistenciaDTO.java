package com.jaapa_back.dto;

import com.jaapa_back.enums.EstadoPagoMultaEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaDTO {
    private String serviceType;
    private String activityType;
    private EstadoPagoMultaEnum estadoPagoMultaEnum;
    private List<AsistenteDTO> attendees;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsistenteDTO {
        private Long id;
        private String status;
        private EstadoPagoMultaEnum estadoPagoMultaEnum;
        private String time;
    }
}
