package com.jaapa_back.dto;

import com.jaapa_back.enums.TipoRegistroEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DireccionDTO {
    private Long id;
    private String callePrincipal;
    private String calleSecundaria;
    private String referencia;
    private String barrio;
    private TipoRegistroEnum tipoRegistro;
}
