package com.bowling.bowlingball.presentationlayer;

import com.bowling.bowlingball.businesslayer.BowlingBallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bowlingballs")
@RequiredArgsConstructor
public class BowlingBallController {

    private final BowlingBallService bowlingBallService;

    @PostMapping
    public ResponseEntity<BowlingBallResponseDTO> createBowlingBall(@Valid @RequestBody BowlingBallRequestDTO request) {
        BowlingBallResponseDTO response = bowlingBallService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BowlingBallResponseDTO> getBowlingBall(@PathVariable String id) {
        BowlingBallResponseDTO response = bowlingBallService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BowlingBallResponseDTO>> getAllBowlingBalls() {
        return ResponseEntity.ok(bowlingBallService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BowlingBallResponseDTO> updateBowlingBall(@PathVariable String id,
                                                                    @Valid @RequestBody BowlingBallRequestDTO request) {
        BowlingBallResponseDTO response = bowlingBallService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBowlingBall(@PathVariable String id) {
        bowlingBallService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
