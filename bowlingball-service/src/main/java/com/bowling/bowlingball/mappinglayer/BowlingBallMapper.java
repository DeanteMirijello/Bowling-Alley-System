package com.bowling.bowlingball.mappinglayer;

import com.bowling.bowlingball.dataaccesslayer.BowlingBall;
import com.bowling.bowlingball.dataaccesslayer.BowlingBallIdentifier;
import com.bowling.bowlingball.presentationlayer.BowlingBallRequestDTO;
import com.bowling.bowlingball.presentationlayer.BowlingBallResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BowlingBallMapper {

    @Mapping(source = "id", target = "id")
    BowlingBallResponseDTO toResponseDTO(BowlingBall entity);

    BowlingBall toEntity(BowlingBallRequestDTO dto);

    default String map(BowlingBallIdentifier value) {
        return value != null ? value.getId() : null;
    }
}
