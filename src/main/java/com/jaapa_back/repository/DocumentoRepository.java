package com.jaapa_back.repository;

import com.jaapa_back.model.Documento;
import com.jaapa_back.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}
