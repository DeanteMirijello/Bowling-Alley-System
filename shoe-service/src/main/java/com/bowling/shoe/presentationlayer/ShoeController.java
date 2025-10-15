package com.bowling.shoe.presentationlayer;

import com.bowling.shoe.businesslayer.ShoeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoes")
@RequiredArgsConstructor
public class ShoeController {

    private final ShoeService shoeService;

    @PostMapping
    public ResponseEntity<ShoeResponseDTO> createShoe(@Valid @RequestBody ShoeRequestDTO request) {
        ShoeResponseDTO response = shoeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoeResponseDTO> getShoe(@PathVariable String id) {
        ShoeResponseDTO response = shoeService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShoeResponseDTO>> getAllShoes() {
        return ResponseEntity.ok(shoeService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoeResponseDTO> updateShoe(@PathVariable String id,
                                                      @Valid @RequestBody ShoeRequestDTO request) {
        ShoeResponseDTO response = shoeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoe(@PathVariable String id) {
        shoeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
