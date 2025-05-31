package com.jaapa_back.repository;

import com.jaapa_back.model.PagoDiferidoSolicitud;
import com.jaapa_back.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PagoDiferidosRepository extends JpaRepository<PagoDiferidoSolicitud, Long>{
}
