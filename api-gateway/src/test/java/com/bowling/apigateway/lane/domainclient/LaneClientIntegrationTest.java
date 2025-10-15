package com.bowling.apigateway.lane.domainclient;

import com.bowling.apigateway.exceptions.InvalidInputException;
import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.lane.presentation.LaneRequestDTO;
import com.bowling.apigateway.lane.presentation.LaneResponseDTO;
import com.bowling.apigateway.lane.presentation.LaneStatus;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LaneClientIntegrationTest {

    private MockWebServer mockWebServer;
    private LaneClient laneClient;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String validId = UUID.randomUUID().toString();

    @BeforeAll
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        laneClient = new LaneClient(webClient);
    }

    @AfterAll
    void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    private LaneRequestDTO buildRequest() {
        return LaneRequestDTO.builder()
                .laneNumber(3)
                .zone("B")
                .status(LaneStatus.AVAILABLE)
                .build();
    }

    private LaneResponseDTO buildResponse(String id) {
        return LaneResponseDTO.builder()
                .id(id)
                .laneNumber(3)
                .zone("B")
                .status(LaneStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenCreate_thenReturnsResponse() throws Exception {
        LaneResponseDTO mock = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mock))
                .addHeader("Content-Type", "application/json"));

        LaneResponseDTO result = laneClient.create(buildRequest());

        assertThat(result.getId()).isEqualTo(validId);
    }

    @Test
    void whenGet_thenReturnsResponse() throws Exception {
        LaneResponseDTO mock = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mock))
                .addHeader("Content-Type", "application/json"));

        LaneResponseDTO result = laneClient.get(validId);

        assertThat(result.getLaneNumber()).isEqualTo(3);
    }

    @Test
    void whenGet_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> laneClient.get("bad-id"));
    }

    @Test
    void whenGet_notFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> laneClient.get(validId));
    }

    @Test
    void whenGetAll_thenReturnsList() throws Exception {
        List<LaneResponseDTO> mockList = List.of(buildResponse("1"), buildResponse("2"));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockList))
                .addHeader("Content-Type", "application/json"));

        List<LaneResponseDTO> result = laneClient.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void whenUpdate_thenReturnsResponse() throws Exception {
        LaneResponseDTO mock = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mock))
                .addHeader("Content-Type", "application/json"));

        LaneResponseDTO result = laneClient.update(validId, buildRequest());

        assertThat(result.getZone()).isEqualTo("B");
    }

    @Test
    void whenUpdate_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> laneClient.update("bad-id", buildRequest()));
    }

    @Test
    void whenDelete_thenNoException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        assertThatCode(() -> laneClient.delete(validId)).doesNotThrowAnyException();
    }

    @Test
    void whenDelete_invalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> laneClient.delete("bad-id"));
    }

    @Test
    void whenDelete_notFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> laneClient.delete(validId));
    }

    @Test
    void whenCreateFailsWith422_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(422)
                .setBody("Invalid zone"));

        assertThrows(InvalidInputException.class, () ->
                laneClient.create(buildRequest()));
    }

    @Test
    void whenGetFailsWith500_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal error"));

        assertThrows(InvalidInputException.class, () -> laneClient.get(validId));
    }

    @Test
    void whenDeleteNotFound_thenThrowsNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> laneClient.delete(validId));
    }


}
