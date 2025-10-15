package com.bowling.apigateway.lane.business;

import com.bowling.apigateway.lane.presentation.LaneRequestDTO;
import com.bowling.apigateway.lane.presentation.LaneResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public interface LaneService {
    EntityModel<LaneResponseDTO> create(LaneRequestDTO request);
    EntityModel<LaneResponseDTO> getById(String id);
    CollectionModel<EntityModel<LaneResponseDTO>> getAll();
    EntityModel<LaneResponseDTO> update(String id, LaneRequestDTO request);
    void delete(String id);
}

