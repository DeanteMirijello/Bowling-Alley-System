package com.bowling.shoe.businesslayer;

import com.bowling.shoe.presentationlayer.ShoeRequestDTO;
import com.bowling.shoe.presentationlayer.ShoeResponseDTO;

import java.util.List;

public interface ShoeService {

    ShoeResponseDTO create(ShoeRequestDTO request);

    ShoeResponseDTO getById(String id);

    List<ShoeResponseDTO> getAll();

    ShoeResponseDTO update(String id, ShoeRequestDTO request);

    void delete(String id);
}
