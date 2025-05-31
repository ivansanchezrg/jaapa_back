package com.jaapa_back.dto;

import com.jaapa_back.enums.EstadoEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudDetalleDTO {

    private UsuarioDTO usuario;
    private DireccionDTO direccion;
    private MedidorDTO medidor;
    private String urlCertificadoInstalacion;

    // Constructor para JPQL
    public SolicitudDetalleDTO(
            Long usuarioId, String usuarioCedula, String usuarioNombre, String usuarioApellido, String usuarioTelefono,
            String usuarioCelular, String usuarioCorreo, Long direccionId, String callePrincipal, String calleSecundaria,
            String referencia, String barrio, Long medidorId, String medidorCodigo, String medidorMarca, String medidorModelo,
            EstadoEnum medidorEstado, String urlCertificadoInstalacion) {

        this.usuario = new UsuarioDTO();
        this.usuario.setId(usuarioId);
        this.usuario.setCedula(usuarioCedula);
        this.usuario.setNombre(usuarioNombre);
        this.usuario.setApellido(usuarioApellido);
        this.usuario.setTelefono(usuarioTelefono);
        this.usuario.setCelular(usuarioCelular);
        this.usuario.setCorreo(usuarioCorreo);

        this.direccion = new DireccionDTO();
        this.direccion.setId(direccionId);
        this.direccion.setCallePrincipal(callePrincipal);
        this.direccion.setCalleSecundaria(calleSecundaria);
        this.direccion.setReferencia(referencia);
        this.direccion.setBarrio(barrio);

        if (medidorId != null) {
            this.medidor = new MedidorDTO();
            this.medidor.setId(medidorId);
            this.medidor.setCodigo(medidorCodigo);
            this.medidor.setMarca(medidorMarca);
            this.medidor.setModelo(medidorModelo);
            this.medidor.setEstado(medidorEstado);
        }
        this.urlCertificadoInstalacion = urlCertificadoInstalacion;
    }
}
