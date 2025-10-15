package com.bowling.lane.businesslayer;

import com.bowling.lane.dataaccesslayer.Lane;
import com.bowling.lane.dataaccesslayer.LaneIdentifier;
import com.bowling.lane.dataaccesslayer.LaneRepository;
import com.bowling.lane.dataaccesslayer.LaneZone;
import com.bowling.lane.exceptionlayer.LaneNotFoundException;
import com.bowling.lane.mappinglayer.LaneMapper;
import com.bowling.lane.presentationlayer.LaneRequestDTO;
import com.bowling.lane.presentationlayer.LaneResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaneServiceImpl implements LaneService {

    private final LaneRepository repository;
    private final LaneMapper mapper;

    public LaneResponseDTO create(LaneRequestDTO request) {
        Lane lane = mapper.toEntity(request);
        lane.setId(LaneIdentifier.generate());
        return mapper.toResponseDTO(repository.save(lane));
    }

    @Override
    public LaneResponseDTO getById(String id) {
        return repository.findById(new LaneIdentifier(id))
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new LaneNotFoundException(id));
    }

    @Override
    public List<LaneResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LaneResponseDTO update(String id, LaneRequestDTO request) {
        Lane existing = repository.findById(new LaneIdentifier(id))
                .orElseThrow(() -> new LaneNotFoundException(id));

        existing.setLaneNumber(request.getLaneNumber());
        existing.setZone(new LaneZone(request.getZone()));
        existing.setStatus(request.getStatus());

        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    public void delete(String id) {
        LaneIdentifier identifier = new LaneIdentifier(id);
        if (!repository.existsById(identifier)) {
            throw new LaneNotFoundException(id);
        }
        repository.deleteById(identifier);
    }
}

