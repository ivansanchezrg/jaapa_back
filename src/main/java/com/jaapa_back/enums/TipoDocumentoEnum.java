package com.jaapa_back.enums;

public enum TipoDocumentoEnum {

    SOLICITUD("Solicitud de servicio", "application/pdf"),
    ACTA("Acta de reunión", "application/pdf"),
    INFORME("Informe técnico", "application/pdf"),
    CONTRATO("Contrato de servicio", "application/pdf");

    private final String descripcion;
    private final String mimeType;

    TipoDocumentoEnum(String descripcion, String mimeType) {
        this.descripcion = descripcion;
        this.mimeType = mimeType;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMimeType() {
        return mimeType;
    }
}
