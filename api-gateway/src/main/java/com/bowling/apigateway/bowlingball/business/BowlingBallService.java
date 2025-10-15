package com.bowling.apigateway.bowlingball.business;

import com.bowling.apigateway.bowlingball.presentation.BowlingBallRequestDTO;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public interface BowlingBallService {
    EntityModel<BowlingBallResponseDTO> create(BowlingBallRequestDTO request);
    EntityModel<BowlingBallResponseDTO> getById(String id);
    CollectionModel<EntityModel<BowlingBallResponseDTO>> getAll();
    EntityModel<BowlingBallResponseDTO> update(String id, BowlingBallRequestDTO request);
    void delete(String id);
}

