package com.bowling.apigateway.transaction.presentation;

import com.bowling.apigateway.transaction.business.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<TransactionResponseDTO>>> getAllTransactions() {
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();

        List<EntityModel<TransactionResponseDTO>> models = transactions.stream()
                .map(this::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(models));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<EntityModel<TransactionResponseDTO>> getTransactionById(@PathVariable String transactionId) {
        TransactionResponseDTO transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(toModel(transaction));
    }

    @PostMapping
    public ResponseEntity<EntityModel<TransactionResponseDTO>> createTransaction(@Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO created = transactionService.createTransaction(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<EntityModel<TransactionResponseDTO>> updateTransaction(
            @PathVariable String transactionId,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO updated = transactionService.updateTransaction(transactionId, requestDTO);
        return ResponseEntity.ok(toModel(updated));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<TransactionResponseDTO> toModel(TransactionResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(TransactionController.class).getTransactionById(dto.getTransactionId())).withSelfRel(),
                linkTo(methodOn(TransactionController.class).getAllTransactions()).withRel("all"));
    }
}


