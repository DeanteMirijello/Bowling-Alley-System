package com.bowling.transaction.domainclientlayer.shoe;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.mockito.Mockito.*;

class ShoeServiceClientTest {

    private ShoeServiceClient client;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final String baseUrl = "http://mock-shoe-service:8080/shoes";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ServiceClientsConfig.ServiceDetails details = new ServiceClientsConfig.ServiceDetails();
        details.setHost("mock-shoe-service");
        details.setPort("8080");

        ServiceClientsConfig config = new ServiceClientsConfig();
        config.setShoeService(details);

        client = new ShoeServiceClient(restTemplate, objectMapper, config);
    }

    @Test
    void whenValidId_thenReturnsShoeModel() {
        ShoeModel mockModel = new ShoeModel();
        String id = "123";

        when(restTemplate.getForObject(baseUrl + "/" + id, ShoeModel.class)).thenReturn(mockModel);

        ShoeModel result = client.getShoeById(id);
        assertNotNull(result);
    }

    @Test
    void whenNotFound_thenThrowsNotFoundException() throws Exception {
        String id = "not-found-id";
        String json = "{\"message\":\"Shoe not found\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Not Found", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, ShoeModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class)).thenReturn(
                new HttpErrorInfo(HttpStatus.NOT_FOUND, "/shoes/" + id, "Shoe not found"));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> client.getShoeById(id));
        assertEquals("Shoe not found", thrown.getMessage());
    }

    @Test
    void whenUnprocessable_thenThrowsInvalidInputException() throws Exception {
        String id = "invalid-id";
        String json = "{\"message\":\"Invalid shoe ID\"}";
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(baseUrl + "/" + id, ShoeModel.class)).thenThrow(ex);
        when(objectMapper.readValue(json, HttpErrorInfo.class)).thenReturn(
                new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/shoes/" + id, "Invalid shoe ID"));

        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> client.getShoeById(id));
        assertEquals("Invalid shoe ID", thrown.getMessage());
    }

    @Test
    void whenUnexpectedError_thenRethrowOriginalException() {
        String id = "unexpected";
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.FORBIDDEN);

        when(restTemplate.getForObject(baseUrl + "/" + id, ShoeModel.class)).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> client.getShoeById(id));
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatusCode());
    }
}