package com.bowling.shoe.mappinglayer;

import com.bowling.shoe.dataaccesslayer.Shoe;
import com.bowling.shoe.dataaccesslayer.ShoeIdentifier;
import com.bowling.shoe.presentationlayer.ShoeRequestDTO;
import com.bowling.shoe.presentationlayer.ShoeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ShoeMapper {

    @Mapping(source = "id", target = "id")
    ShoeResponseDTO toResponseDTO(Shoe entity);

    @Mapping(target = "id", ignore = true)
    Shoe toEntity(ShoeRequestDTO dto);

    default String map(ShoeIdentifier value) {
        return value != null ? value.getId().toString() : null;
    }
}
