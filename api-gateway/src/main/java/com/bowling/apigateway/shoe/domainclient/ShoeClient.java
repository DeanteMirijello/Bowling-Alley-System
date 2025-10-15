package com.bowling.apigateway.shoe.domainclient;

import com.bowling.apigateway.exceptions.*;
import com.bowling.apigateway.shoe.presentation.ShoeRequestDTO;
import com.bowling.apigateway.shoe.presentation.ShoeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShoeClient {

    private final WebClient shoeWebClient;

    public ShoeResponseDTO create(ShoeRequestDTO request) {
        try {
            return shoeWebClient.post()
                    .uri("/shoes")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Shoe: " + msg)))
                    .bodyToMono(ShoeResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public ShoeResponseDTO get(String id) {
        validateUUID(id);
        try {
            return shoeWebClient.get()
                    .uri("/shoes/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            res -> Mono.error(new NotFoundException("Shoe not found: " + id)))
                    .bodyToMono(ShoeResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public List<ShoeResponseDTO> getAll() {
        return shoeWebClient.get()
                .uri("/shoes")
                .retrieve()
                .bodyToFlux(ShoeResponseDTO.class)
                .collectList()
                .block();
    }

    public ShoeResponseDTO update(String id, ShoeRequestDTO request) {
        validateUUID(id);
        try {
            return shoeWebClient.put()
                    .uri("/shoes/{id}", id)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Shoe: " + msg)))
                    .bodyToMono(ShoeResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public void delete(String id) {
        validateUUID(id);
        shoeWebClient.delete()
                .uri("/shoes/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        res -> Mono.error(new NotFoundException("Shoe not found: " + id)))
                .toBodilessEntity()
                .block();
    }

    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid Shoe ID format: " + id);
        }
    }
}

