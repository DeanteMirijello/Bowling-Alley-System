package com.bowling.transaction.domainclientlayer.shoe;

import com.bowling.transaction.config.ServiceClientsConfig;
import com.bowling.transaction.exceptionlayer.HttpErrorInfo;
import com.bowling.transaction.exceptionlayer.InvalidInputException;
import com.bowling.transaction.exceptionlayer.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import java.io.IOException;

@Slf4j
@Component
public class ShoeServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String SHOE_SERVICE_BASE_URL;

    public ShoeServiceClient(RestTemplate restTemplate,
                             ObjectMapper mapper,
                             ServiceClientsConfig config) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.SHOE_SERVICE_BASE_URL = "http://" +
                config.getShoeService().getHost() + ":" +
                config.getShoeService().getPort() + "/shoes";
    }


    public ShoeModel getShoeById(String shoeId) {
        try {
            String url = SHOE_SERVICE_BASE_URL + "/" + shoeId;
            log.debug("Shoe-Service GET by shoeId URL: {}", url);
            return restTemplate.getForObject(url, ShoeModel.class);
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

