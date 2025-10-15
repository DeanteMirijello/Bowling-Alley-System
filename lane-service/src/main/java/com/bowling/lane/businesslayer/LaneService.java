package com.bowling.lane.businesslayer;

import com.bowling.lane.presentationlayer.LaneRequestDTO;
import com.bowling.lane.presentationlayer.LaneResponseDTO;

import java.util.List;

public interface LaneService {

    LaneResponseDTO create(LaneRequestDTO request);

    LaneResponseDTO getById(String id);

    List<LaneResponseDTO> getAll();

    LaneResponseDTO update(String id, LaneRequestDTO request);

    void delete(String id);
}

