package com.jaapa_back.dto.mapper;

import com.jaapa_back.dto.TarifaDTO;
import com.jaapa_back.model.Tarifa;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TarifaMapper {

    TarifaDTO toDto(Tarifa tarifa);

}
