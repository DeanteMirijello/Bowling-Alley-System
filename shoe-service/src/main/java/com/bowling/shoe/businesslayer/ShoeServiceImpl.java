package com.bowling.shoe.businesslayer;

import com.bowling.shoe.dataaccesslayer.Shoe;
import com.bowling.shoe.dataaccesslayer.ShoeIdentifier;
import com.bowling.shoe.dataaccesslayer.ShoeRepository;
import com.bowling.shoe.exceptionlayer.ShoeNotFoundException;
import com.bowling.shoe.mappinglayer.ShoeMapper;
import com.bowling.shoe.presentationlayer.ShoeRequestDTO;
import com.bowling.shoe.presentationlayer.ShoeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoeServiceImpl implements ShoeService {

    private final ShoeRepository repository;
    private final ShoeMapper mapper;

    @Override
    public ShoeResponseDTO create(ShoeRequestDTO request) {
        Shoe shoe = mapper.toEntity(request)
                .toBuilder()
                .id(ShoeIdentifier.generate())
                .build();

        return mapper.toResponseDTO(repository.save(shoe));
    }

    @Override
    public ShoeResponseDTO getById(String id) {
        return repository.findById(ShoeIdentifier.fromString(id))
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ShoeNotFoundException(id));
    }

    @Override
    public List<ShoeResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ShoeResponseDTO update(String id, ShoeRequestDTO request) {
        Shoe existing = repository.findById(ShoeIdentifier.fromString(id))
                .orElseThrow(() -> new ShoeNotFoundException(id));

        existing.setSize(request.getSize());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setStatus(request.getStatus());

        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    public void delete(String id) {
        if (repository.findById(ShoeIdentifier.fromString(id)).isEmpty()) {
            throw new ShoeNotFoundException(id);
        }
        repository.deleteById(ShoeIdentifier.fromString(id));
    }
}
