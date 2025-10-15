package com.bowling.transaction.presentationlayer;

import com.bowling.transaction.businesslayer.TransactionService;
import com.bowling.transaction.exceptionlayer.InvalidInputException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable String transactionId) {
        validateUUID(transactionId);
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(requestDTO));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable String transactionId,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        validateUUID(transactionId);
        return ResponseEntity.ok(transactionService.updateTransaction(transactionId, requestDTO));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String transactionId) {
        validateUUID(transactionId);
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid UUID format: " + id);
        }
    }
}

