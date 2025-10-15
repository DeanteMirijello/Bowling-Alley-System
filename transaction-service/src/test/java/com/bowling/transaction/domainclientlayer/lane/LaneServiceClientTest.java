package com.bowling.transaction.domainclientlayer.lane;

import static org.junit.jupiter.api.Assertions.*;

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
import static org.mockito.Mockito.*;

class LaneServiceClientTest {

    private LaneServiceClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://mock-lane-service:8080/lanes";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ServiceClientsConfig.ServiceDetails details = new ServiceClientsConfig.ServiceDetails();
        details.setHost("mock-lane-service");
        details.setPort("8080");

        ServiceClientsConfig config = new ServiceClientsConfig();
        config.setLaneService(details);

        client = new LaneServiceClient(restTemplate, objectMapper, config);
    }

    @Test
    void whenValidId_thenReturnsLaneModel() {
        LaneModel mockModel = new LaneModel();
        String id = "123";

        when(restTemplate.getForObject(baseUrl + "/" + id, LaneModel.class)).thenReturn(mockModel);

        LaneModel result = client.getLaneByLaneId(id);
        assertNotNull(result);
    }

    @Test
    void whenNotFound_thenThrowsNotFoundException() throws Exception {
        String id = "not-found-id";
        String json = "{\"message\":\"Lane not found\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Not Found", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, LaneModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class)).thenReturn(
                new HttpErrorInfo(HttpStatus.NOT_FOUND, "/lanes/" + id, "Lane not found"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> client.getLaneByLaneId(id));
        assertEquals("Lane not found", thrown.getMessage());
    }

    @Test
    void whenUnprocessable_thenThrowsInvalidInputException() throws Exception {
        String id = "invalid-id";
        String json = "{\"message\":\"Invalid lane ID\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, LaneModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class)).thenReturn(
                new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/lanes/" + id, "Invalid lane ID"));

        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> client.getLaneByLaneId(id));
        assertEquals("Invalid lane ID", thrown.getMessage());
    }

    @Test
    void whenUnexpectedError_thenRethrowOriginalException() {
        String id = "unexpected";
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.FORBIDDEN);

        when(restTemplate.getForObject(baseUrl + "/" + id, LaneModel.class)).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> client.getLaneByLaneId(id));
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatusCode());
    }
}