package com.jaapa_back.service;

import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import com.jaapa_back.model.Solicitud;
import com.jaapa_back.model.Usuario;
import com.jaapa_back.repository.TipoSolicitudRepository;
import com.jaapa_back.repository.UsuarioRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que gestiona las operaciones relacionadas con usuarios.
 * Proporciona métodos para consultar y gestionar información de usuarios.
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VerificarCedulaService verificarCedulaService;


    /**
     * Busca un usuario en la base de datos utilizando su cédula.
     * @param cedula La cédula del usuario a buscar.
     * @return El Usuario si se encuentra, de lo contrario null.
     */
    public Usuario findByCedula(String cedula) {
        verificarCedulaService.verificarCedula(cedula);
        return usuarioRepository.findUserByCedula(cedula);
    }

    /**
     * Guarda un usuario en la base de datos.
     *
     * @param usuario El usuario a guardar.
     * @return El usuario guardado.
     * @throws EntityExistsException si ya existe un usuario con la misma cédula.
     */
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

}
