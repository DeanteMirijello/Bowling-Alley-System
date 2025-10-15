package com.bowling.transaction.domainclientlayer.bowlingball;


import com.bowling.transaction.config.ServiceClientsConfig;
import com.bowling.transaction.exceptionlayer.HttpErrorInfo;
import com.bowling.transaction.exceptionlayer.InvalidInputException;
import com.bowling.transaction.exceptionlayer.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class BowlingBallServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String BALL_SERVICE_BASE_URL;

    public BowlingBallServiceClient(RestTemplate restTemplate,
                                    ObjectMapper mapper,
                                    ServiceClientsConfig config) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.BALL_SERVICE_BASE_URL = "http://" +
                config.getBowlingballService().getHost() + ":" +
                config.getBowlingballService().getPort() + "/bowlingballs";
    }


    public BowlingBallModel getBowlingBallById(String ballId) {
        try {
            String url = BALL_SERVICE_BASE_URL + "/" + ballId;
            log.debug("BowlingBall-Service GET by ballId URL: {}", url);
            return restTemplate.getForObject(url, BowlingBallModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }
        log.warn("Unexpected HTTP error: {}, body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        return ex;
    }
}
