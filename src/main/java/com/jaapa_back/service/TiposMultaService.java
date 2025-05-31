package com.jaapa_back.service;


import com.jaapa_back.model.TiposActividad;
import com.jaapa_back.repository.TiposActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiposMultaService {
    private final TiposActividadRepository tipoRepository;

    @Autowired
    public TiposMultaService(TiposActividadRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }



    /**
     * Retorna todos los tipos de multas.
     *
     * @return Iterable de Tipo.
     */
    public Iterable<TiposActividad> findAll() {
        return tipoRepository.findAll();
    }


}
