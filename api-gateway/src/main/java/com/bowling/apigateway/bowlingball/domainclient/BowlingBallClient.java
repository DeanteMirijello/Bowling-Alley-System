package com.bowling.apigateway.bowlingball.domainclient;

import com.bowling.apigateway.bowlingball.presentation.BowlingBallRequestDTO;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallResponseDTO;
import com.bowling.apigateway.exceptions.*;
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
public class BowlingBallClient {

    private final WebClient bowlingBallWebClient;

    public BowlingBallResponseDTO createBall(BowlingBallRequestDTO request) {
        try {
            return bowlingBallWebClient.post()
                    .uri("/bowlingballs")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Bowling Ball: " + msg)))
                    .bodyToMono(BowlingBallResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public BowlingBallResponseDTO getBall(String id) {
        validateUUID(id);
        try {
            return bowlingBallWebClient.get()
                    .uri("/bowlingballs/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            res -> Mono.error(new NotFoundException("Bowling ball not found: " + id)))
                    .bodyToMono(BowlingBallResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public List<BowlingBallResponseDTO> getAll() {
        return bowlingBallWebClient.get()
                .uri("/bowlingballs")
                .retrieve()
                .bodyToFlux(BowlingBallResponseDTO.class)
                .collectList()
                .block();
    }

    public BowlingBallResponseDTO updateBall(String id, BowlingBallRequestDTO request) {
        validateUUID(id);
        try {
            return bowlingBallWebClient.put()
                    .uri("/bowlingballs/{id}", id)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Bowling Ball: " + msg)))
                    .bodyToMono(BowlingBallResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public void deleteBall(String id) {
        validateUUID(id);
        bowlingBallWebClient.delete()
                .uri("/bowlingballs/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        res -> Mono.error(new NotFoundException("Bowling ball not found: " + id)))
                .toBodilessEntity()
                .block();
    }

    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid BowlingBall ID format: " + id);
        }
    }
}
