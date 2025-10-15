package com.bowling.bowlingball.businesslayer;

import com.bowling.bowlingball.dataaccesslayer.BowlingBall;
import com.bowling.bowlingball.dataaccesslayer.BowlingBallIdentifier;
import com.bowling.bowlingball.dataaccesslayer.BowlingBallRepository;
import com.bowling.bowlingball.exceptionlayer.BowlingBallNotFoundException;
import com.bowling.bowlingball.mappinglayer.BowlingBallMapper;
import com.bowling.bowlingball.presentationlayer.BowlingBallRequestDTO;
import com.bowling.bowlingball.presentationlayer.BowlingBallResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BowlingBallServiceImpl implements BowlingBallService {

    private final BowlingBallRepository repository;
    private final BowlingBallMapper mapper;

    @Override
    public BowlingBallResponseDTO create(BowlingBallRequestDTO request) {
        BowlingBall ball = mapper.toEntity(request);
        ball.setId(BowlingBallIdentifier.generate());
        return mapper.toResponseDTO(repository.save(ball));
    }

    @Override
    public BowlingBallResponseDTO getById(String id) {
        return repository.findById(new BowlingBallIdentifier(id))
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new BowlingBallNotFoundException(id));
    }

    @Override
    public List<BowlingBallResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BowlingBallResponseDTO update(String id, BowlingBallRequestDTO request) {
        BowlingBall existing = repository.findById(new BowlingBallIdentifier(id))
                .orElseThrow(() -> new BowlingBallNotFoundException(id));

        existing.setSize(request.getSize());
        existing.setGripType(request.getGripType());
        existing.setColor(request.getColor());
        existing.setStatus(request.getStatus());

        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    public void delete(String id) {
        BowlingBallIdentifier identifier = new BowlingBallIdentifier(id);
        if (!repository.existsById(identifier)) {
            throw new BowlingBallNotFoundException(id);
        }
        repository.deleteById(identifier);
    }
}