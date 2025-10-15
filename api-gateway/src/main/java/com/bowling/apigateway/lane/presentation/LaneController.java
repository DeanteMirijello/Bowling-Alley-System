package com.bowling.apigateway.lane.presentation;

import com.bowling.apigateway.lane.business.LaneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lanes")
@RequiredArgsConstructor
public class LaneController {

    private final LaneService laneService;

    @PostMapping
    public ResponseEntity<EntityModel<LaneResponseDTO>> create(@Valid @RequestBody LaneRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laneService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<LaneResponseDTO>> getById(@PathVariable String id) {
        return ResponseEntity.ok(laneService.getById(id));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<LaneResponseDTO>>> getAll() {
        return ResponseEntity.ok(laneService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<LaneResponseDTO>> update(@PathVariable String id,
                                                               @Valid @RequestBody LaneRequestDTO request) {
        return ResponseEntity.ok(laneService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        laneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

