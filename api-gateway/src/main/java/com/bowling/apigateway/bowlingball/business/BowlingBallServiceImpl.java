package com.bowling.apigateway.bowlingball.business;

import com.bowling.apigateway.bowlingball.domainclient.BowlingBallClient;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallController;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallRequestDTO;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class BowlingBallServiceImpl implements BowlingBallService {

    private final BowlingBallClient bowlingBallClient;

    @Override
    public EntityModel<BowlingBallResponseDTO> create(BowlingBallRequestDTO request) {
        BowlingBallResponseDTO response = bowlingBallClient.createBall(request);
        return toModel(response);
    }

    @Override
    public EntityModel<BowlingBallResponseDTO> getById(String id) {
        BowlingBallResponseDTO response = bowlingBallClient.getBall(id);
        return toModel(response);
    }

    @Override
    public CollectionModel<EntityModel<BowlingBallResponseDTO>> getAll() {
        List<BowlingBallResponseDTO> list = bowlingBallClient.getAll();
        List<EntityModel<BowlingBallResponseDTO>> models = list.stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(models);
    }

    @Override
    public EntityModel<BowlingBallResponseDTO> update(String id, BowlingBallRequestDTO request) {
        BowlingBallResponseDTO response = bowlingBallClient.updateBall(id, request);
        return toModel(response);
    }

    @Override
    public void delete(String id) {
        bowlingBallClient.deleteBall(id);
    }

    private EntityModel<BowlingBallResponseDTO> toModel(BowlingBallResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(BowlingBallController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(BowlingBallController.class).getAll()).withRel("all"));
    }
}

