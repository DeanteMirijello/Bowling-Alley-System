package com.bowling.apigateway.lane.domainclient;

import com.bowling.apigateway.exceptions.*;
import com.bowling.apigateway.lane.presentation.LaneRequestDTO;
import com.bowling.apigateway.lane.presentation.LaneResponseDTO;
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
public class LaneClient {

    private final WebClient laneWebClient;

    public LaneResponseDTO create(LaneRequestDTO request) {
        try {
            return laneWebClient.post()
                    .uri("/lanes")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Lane: " + msg)))
                    .bodyToMono(LaneResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public LaneResponseDTO get(String id) {
        validateUUID(id);
        try {
            return laneWebClient.get()
                    .uri("/lanes/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            res -> Mono.error(new NotFoundException("Lane not found: " + id)))
                    .bodyToMono(LaneResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public List<LaneResponseDTO> getAll() {
        return laneWebClient.get()
                .uri("/lanes")
                .retrieve()
                .bodyToFlux(LaneResponseDTO.class)
                .collectList()
                .block();
    }

    public LaneResponseDTO update(String id, LaneRequestDTO request) {
        validateUUID(id);
        try {
            return laneWebClient.put()
                    .uri("/lanes/{id}", id)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            res -> res.bodyToMono(String.class)
                                    .map(msg -> new InvalidInputException("Lane: " + msg)))
                    .bodyToMono(LaneResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new InvalidInputException("Downstream error: " + e.getResponseBodyAsString());
        }
    }

    public void delete(String id) {
        validateUUID(id);
        laneWebClient.delete()
                .uri("/lanes/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        res -> Mono.error(new NotFoundException("Lane not found: " + id)))
                .toBodilessEntity()
                .block();
    }

    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid Lane ID format: " + id);
        }
    }
}
