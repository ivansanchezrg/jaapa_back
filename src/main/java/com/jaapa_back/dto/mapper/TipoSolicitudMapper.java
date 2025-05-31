package com.jaapa_back.dto.mapper;

import com.jaapa_back.dto.response.TipoSolicitudResponseDTO;
import com.jaapa_back.model.TipoSolicitud;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TarifaMapper.class})
public interface TipoSolicitudMapper {

    TipoSolicitudResponseDTO toDto(TipoSolicitud tipoSolicitud);
    List<TipoSolicitudResponseDTO> toDtoList(Iterable<TipoSolicitud> tiposSolicitud);
}
