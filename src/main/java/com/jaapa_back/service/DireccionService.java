package com.jaapa_back.service;

import com.jaapa_back.model.Direccion;
import com.jaapa_back.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    /**
     * Crea una nuevo direccion.
     *
     * @param direccion La direccion a guardar.
     * @return La direccion guardada.
     */
    public Direccion save(Direccion direccion) {
        return direccionRepository.save(direccion);
    }
}
