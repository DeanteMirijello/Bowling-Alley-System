package com.bowling.bowlingball.businesslayer;

import com.bowling.bowlingball.presentationlayer.BowlingBallRequestDTO;
import com.bowling.bowlingball.presentationlayer.BowlingBallResponseDTO;

import java.util.List;

public interface BowlingBallService {

    BowlingBallResponseDTO create(BowlingBallRequestDTO request);

    BowlingBallResponseDTO getById(String id);

    List<BowlingBallResponseDTO> getAll();

    BowlingBallResponseDTO update(String id, BowlingBallRequestDTO request);

    void delete(String id);
}
