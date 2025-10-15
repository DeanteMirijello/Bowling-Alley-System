package com.bowling.apigateway.lane.business;

import com.bowling.apigateway.lane.domainclient.LaneClient;
import com.bowling.apigateway.lane.presentation.LaneController;
import com.bowling.apigateway.lane.presentation.LaneRequestDTO;
import com.bowling.apigateway.lane.presentation.LaneResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class LaneServiceImpl implements LaneService {

    private final LaneClient laneClient;

    @Override
    public EntityModel<LaneResponseDTO> create(LaneRequestDTO request) {
        LaneResponseDTO response = laneClient.create(request);
        return toModel(response);
    }

    @Override
    public EntityModel<LaneResponseDTO> getById(String id) {
        return toModel(laneClient.get(id));
    }

    @Override
    public CollectionModel<EntityModel<LaneResponseDTO>> getAll() {
        List<LaneResponseDTO> lanes = laneClient.getAll();
        List<EntityModel<LaneResponseDTO>> models = lanes.stream().map(this::toModel).toList();
        return CollectionModel.of(models);
    }

    @Override
    public EntityModel<LaneResponseDTO> update(String id, LaneRequestDTO request) {
        return toModel(laneClient.update(id, request));
    }

    @Override
    public void delete(String id) {
        laneClient.delete(id);
    }

    private EntityModel<LaneResponseDTO> toModel(LaneResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(LaneController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(LaneController.class).getAll()).withRel("all"));
    }
}

