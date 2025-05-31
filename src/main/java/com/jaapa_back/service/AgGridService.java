package com.jaapa_back.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AgGridService {

    /* =====================================================
    *  METODOS REUTILIZABLES PARA AG-GRID
    * ====================================================== */

    /**
     * Aplica ordenamiento por defecto a un objeto Pageable si no tiene ordenamiento especificado.
     *
     * @param pageable el objeto Pageable a evaluar
     * @return el Pageable original si ya tiene ordenamiento, o uno nuevo con ordenamiento
     *         descendente por ID si no lo tiene
     */
    public Pageable aplicarOrdenamientoDefault(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id")
            );
        }
        return pageable;
    }

    /**
     * Convierte un String a LocalDate, retornando null si el String es null.
     *
     * @param fecha String en formato ISO (yyyy-MM-dd)
     * @return LocalDate parseado o null
     */
    public LocalDate parseFecha(String fecha) {
        return fecha != null ? LocalDate.parse(fecha) : null;
    }
}
