package com.bowling.apigateway.shoe.domainclient;

import com.bowling.apigateway.exceptions.InvalidInputException;
import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.shoe.presentation.ShoeRequestDTO;
import com.bowling.apigateway.shoe.presentation.ShoeResponseDTO;
import com.bowling.apigateway.shoe.presentation.ShoeSize;
import com.bowling.apigateway.shoe.presentation.ShoeStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoeClientIntegrationTest {

    private MockWebServer mockWebServer;

    private ShoeClient shoeClient;

    @BeforeAll
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient client = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        shoeClient = new ShoeClient(client);

        mapper.registerModule(new JavaTimeModule());
    }

    @AfterAll
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String validId = UUID.randomUUID().toString();

    private ShoeRequestDTO buildRequest() {
        return ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_9)
                .purchaseDate(LocalDate.of(2023, 1, 1))
                .status(ShoeStatus.AVAILABLE)
                .build();
    }

    private ShoeResponseDTO buildResponse(String id) {
        return ShoeResponseDTO.builder()
                .id(id)
                .size(ShoeSize.SIZE_9)
                .purchaseDate(LocalDate.of(2023, 1, 1))
                .status(ShoeStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenCreate_thenReturnsResponse() throws Exception {
        ShoeResponseDTO mockResponse = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

        ShoeResponseDTO result = shoeClient.create(buildRequest());

        assertThat(result.getId()).isEqualTo(validId);
    }

    @Test
    void whenGet_thenReturnsResponse() throws Exception {
        ShoeResponseDTO mockResponse = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

        ShoeResponseDTO result = shoeClient.get(validId);

        assertThat(result.getSize()).isEqualTo(ShoeSize.SIZE_9);
    }

    @Test
    void whenGet_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> shoeClient.get("not-a-uuid"));
    }

    @Test
    void whenGet_notFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> shoeClient.get(validId));
    }

    @Test
    void whenGetAll_thenReturnsList() throws Exception {
        List<ShoeResponseDTO> mockList = List.of(buildResponse("1"), buildResponse("2"));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockList))
                .addHeader("Content-Type", "application/json"));

        List<ShoeResponseDTO> result = shoeClient.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void whenUpdate_thenReturnsUpdated() throws Exception {
        ShoeResponseDTO updated = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(updated))
                .addHeader("Content-Type", "application/json"));

        ShoeResponseDTO result = shoeClient.update(validId, buildRequest());

        assertThat(result.getId()).isEqualTo(validId);
    }

    @Test
    void whenUpdate_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> shoeClient.update("bad-id", buildRequest()));
    }

    @Test
    void whenDelete_thenSucceeds() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        assertThatCode(() -> shoeClient.delete(validId)).doesNotThrowAnyException();
    }

    @Test
    void whenDelete_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> shoeClient.delete("bad-id"));
    }

    @Test
    void whenDelete_notFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> shoeClient.delete(validId));
    }

    @Test
    void whenCreateFailsWith422_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(422)
                .setBody("shoe validation failed"));

        assertThrows(InvalidInputException.class, () -> shoeClient.create(buildRequest()));
    }

    @Test
    void whenGetFailsWith500_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("shoe GET failed"));

        assertThrows(InvalidInputException.class, () -> shoeClient.get(validId));
    }

    @Test
    void whenDeleteReturns404_thenThrowsNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> shoeClient.delete(validId));
    }

}
