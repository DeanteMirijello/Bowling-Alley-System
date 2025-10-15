package com.bowling.lane.presentationlayer;

import com.bowling.lane.businesslayer.LaneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lanes")
@RequiredArgsConstructor
public class LaneController {

    private final LaneService laneService;

    @PostMapping
    public ResponseEntity<LaneResponseDTO> createLane(@Valid @RequestBody LaneRequestDTO request) {
        LaneResponseDTO response = laneService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaneResponseDTO> getLane(@PathVariable String id) {
        LaneResponseDTO response = laneService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LaneResponseDTO>> getAllLanes() {
        return ResponseEntity.ok(laneService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaneResponseDTO> updateLane(@PathVariable String id,
                                                      @Valid @RequestBody LaneRequestDTO request) {
        LaneResponseDTO response = laneService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLane(@PathVariable String id) {
        laneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

