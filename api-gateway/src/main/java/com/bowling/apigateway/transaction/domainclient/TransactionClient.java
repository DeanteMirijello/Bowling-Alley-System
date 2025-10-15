package com.bowling.apigateway.transaction.domainclient;

import com.bowling.apigateway.exceptions.ErrorResponse;
import com.bowling.apigateway.exceptions.InvalidInputException;
import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.transaction.presentation.TransactionRequestDTO;
import com.bowling.apigateway.transaction.presentation.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionClient {

    @Qualifier("transactionWebClient")
    private final WebClient transactionWebClient;

    public TransactionClient(@Qualifier("transactionWebClient") WebClient transactionWebClient) {
        this.transactionWebClient = transactionWebClient;
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionWebClient.get()
                .uri("/api/transactions")
                .retrieve()
                .bodyToFlux(TransactionResponseDTO.class)
                .collectList()
                .block();
    }

    public TransactionResponseDTO getTransactionById(String transactionId) {
        validateUUID(transactionId);

        try {
            return transactionWebClient.get()
                    .uri("/api/transactions/{id}", transactionId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            res -> Mono.error(new NotFoundException("Transaction not found: " + transactionId)))
                    .bodyToMono(TransactionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        try {
            return transactionWebClient.post()
                    .uri("/api/transactions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(InvalidInputException::new))
                    .bodyToMono(TransactionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public TransactionResponseDTO updateTransaction(String transactionId, TransactionRequestDTO request) {
        validateUUID(transactionId);

        try {
            return transactionWebClient.put()
                    .uri("/api/transactions/{id}", transactionId)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(InvalidInputException::new))
                    .bodyToMono(TransactionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public void deleteTransaction(String transactionId) {
        validateUUID(transactionId);

        transactionWebClient.delete()
                .uri("/api/transactions/{id}", transactionId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        res -> Mono.error(new NotFoundException("Transaction not found: " + transactionId)))
                .toBodilessEntity()
                .block();
    }

    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid UUID format: " + id);
        }
    }
}


