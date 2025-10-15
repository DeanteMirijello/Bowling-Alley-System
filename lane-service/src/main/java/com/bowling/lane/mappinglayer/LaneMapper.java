package com.bowling.lane.mappinglayer;

import com.bowling.lane.dataaccesslayer.Lane;
import com.bowling.lane.dataaccesslayer.LaneIdentifier;
import com.bowling.lane.dataaccesslayer.LaneZone;
import com.bowling.lane.presentationlayer.LaneRequestDTO;
import com.bowling.lane.presentationlayer.LaneResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LaneMapper {


    @Mapping(source = "id", target = "id")
    @Mapping(source = "zone.zone", target = "zone")
    LaneResponseDTO toResponseDTO(Lane entity);

    @Mapping(source = "zone", target = "zone")
    Lane toEntity(LaneRequestDTO dto);

    default String map(LaneIdentifier value) {
        return value != null ? value.getId() : null;
    }

    default LaneZone map(String value) {
        return new LaneZone(value);
    }
}



