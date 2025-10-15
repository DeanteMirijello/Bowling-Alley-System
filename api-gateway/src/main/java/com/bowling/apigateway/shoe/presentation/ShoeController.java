package com.bowling.apigateway.shoe.presentation;

import com.bowling.apigateway.shoe.business.ShoeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/shoes")
@RequiredArgsConstructor
public class ShoeController {

    private final ShoeService shoeService;

    @PostMapping
    public ResponseEntity<EntityModel<ShoeResponseDTO>> create(@Valid @RequestBody ShoeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shoeService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ShoeResponseDTO>> getById(@PathVariable String id) {
        return ResponseEntity.ok(shoeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ShoeResponseDTO>>> getAll() {
        return ResponseEntity.ok(shoeService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ShoeResponseDTO>> update(@PathVariable String id, @Valid @RequestBody ShoeRequestDTO request) {
        return ResponseEntity.ok(shoeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        shoeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

