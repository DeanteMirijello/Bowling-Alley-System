package com.bowling.transaction.domainclientlayer.bowlingball;

import com.bowling.transaction.config.ServiceClientsConfig;
import com.bowling.transaction.exceptionlayer.HttpErrorInfo;
import com.bowling.transaction.exceptionlayer.InvalidInputException;
import com.bowling.transaction.exceptionlayer.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BowlingBallServiceClientTest {

    private BowlingBallServiceClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://mock-bowlingball-service:8080/bowlingballs";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ServiceClientsConfig.ServiceDetails details = new ServiceClientsConfig.ServiceDetails();
        details.setHost("mock-bowlingball-service");
        details.setPort("8080");

        ServiceClientsConfig config = new ServiceClientsConfig();
        config.setBowlingballService(details);

        client = new BowlingBallServiceClient(restTemplate, objectMapper, config);
    }

    @Test
    void whenValidId_thenReturnsBowlingBallModel() {
        BowlingBallModel mockModel = new BowlingBallModel();
        String id = "123";

        when(restTemplate.getForObject(baseUrl + "/" + id, BowlingBallModel.class)).thenReturn(mockModel);

        BowlingBallModel result = client.getBowlingBallById(id);
        assertNotNull(result);
    }

    @Test
    void whenNotFound_thenThrowsNotFoundException() throws Exception {
        String id = "missing-id";
        String json = "{\"message\":\"Ball not found\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Not Found", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, BowlingBallModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "/bowlingballs/" + id, "Ball not found"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> client.getBowlingBallById(id));
        assertEquals("Ball not found", thrown.getMessage());
    }

    @Test
    void whenUnprocessable_thenThrowsInvalidInputException() throws Exception {
        String id = "bad-id";
        String json = "{\"message\":\"Invalid ball ID\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, BowlingBallModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/bowlingballs/" + id, "Invalid ball ID"));

        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> client.getBowlingBallById(id));
        assertEquals("Invalid ball ID", thrown.getMessage());
    }

    @Test
    void whenUnexpectedError_thenRethrowOriginalException() {
        String id = "oops";
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.FORBIDDEN);

        when(restTemplate.getForObject(baseUrl + "/" + id, BowlingBallModel.class)).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> client.getBowlingBallById(id));
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatusCode());
    }
}
