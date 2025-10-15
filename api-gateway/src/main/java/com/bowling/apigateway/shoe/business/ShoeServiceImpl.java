package com.bowling.apigateway.shoe.business;

import com.bowling.apigateway.shoe.domainclient.ShoeClient;
import com.bowling.apigateway.shoe.presentation.ShoeController;
import com.bowling.apigateway.shoe.presentation.ShoeRequestDTO;
import com.bowling.apigateway.shoe.presentation.ShoeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class ShoeServiceImpl implements ShoeService {

    private final ShoeClient shoeClient;

    @Override
    public EntityModel<ShoeResponseDTO> create(ShoeRequestDTO request) {
        ShoeResponseDTO response = shoeClient.create(request);
        return toModel(response);
    }

    @Override
    public EntityModel<ShoeResponseDTO> getById(String id) {
        return toModel(shoeClient.get(id));
    }

    @Override
    public CollectionModel<EntityModel<ShoeResponseDTO>> getAll() {
        List<ShoeResponseDTO> shoes = shoeClient.getAll();
        List<EntityModel<ShoeResponseDTO>> models = shoes.stream().map(this::toModel).toList();
        return CollectionModel.of(models);
    }

    @Override
    public EntityModel<ShoeResponseDTO> update(String id, ShoeRequestDTO request) {
        return toModel(shoeClient.update(id, request));
    }

    @Override
    public void delete(String id) {
        shoeClient.delete(id);
    }

    private EntityModel<ShoeResponseDTO> toModel(ShoeResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ShoeController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(ShoeController.class).getAll()).withRel("all"));
    }
}
