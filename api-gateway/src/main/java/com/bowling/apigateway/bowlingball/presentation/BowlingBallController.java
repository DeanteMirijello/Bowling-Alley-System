package com.bowling.apigateway.bowlingball.presentation;

import com.bowling.apigateway.bowlingball.business.BowlingBallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balls")
@RequiredArgsConstructor
public class BowlingBallController {

    private final BowlingBallService bowlingBallService;

    @PostMapping
    public ResponseEntity<EntityModel<BowlingBallResponseDTO>> create(@Valid @RequestBody BowlingBallRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bowlingBallService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BowlingBallResponseDTO>> getById(@PathVariable String id) {
        return ResponseEntity.ok(bowlingBallService.getById(id));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BowlingBallResponseDTO>>> getAll() {
        return ResponseEntity.ok(bowlingBallService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<BowlingBallResponseDTO>> update(@PathVariable String id,
                                                                      @Valid @RequestBody BowlingBallRequestDTO request) {
        return ResponseEntity.ok(bowlingBallService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bowlingBallService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

