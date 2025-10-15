package com.bowling.apigateway.shoe.business;

import com.bowling.apigateway.shoe.presentation.ShoeRequestDTO;
import com.bowling.apigateway.shoe.presentation.ShoeResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public interface ShoeService {
    EntityModel<ShoeResponseDTO> create(ShoeRequestDTO request);
    EntityModel<ShoeResponseDTO> getById(String id);
    CollectionModel<EntityModel<ShoeResponseDTO>> getAll();
    EntityModel<ShoeResponseDTO> update(String id, ShoeRequestDTO request);
    void delete(String id);
}

