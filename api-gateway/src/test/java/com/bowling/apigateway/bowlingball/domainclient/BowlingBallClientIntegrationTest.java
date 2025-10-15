package com.bowling.apigateway.bowlingball.domainclient;

import com.bowling.apigateway.bowlingball.presentation.BallSize;
import com.bowling.apigateway.bowlingball.presentation.BallStatus;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallRequestDTO;
import com.bowling.apigateway.bowlingball.presentation.BowlingBallResponseDTO;
import com.bowling.apigateway.exceptions.InvalidInputException;
import com.bowling.apigateway.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BowlingBallClientIntegrationTest {

    private MockWebServer mockWebServer;

    private BowlingBallClient bowlingBallClient;

    private String validId;

    private BowlingBallRequestDTO buildRequest() {
        return BowlingBallRequestDTO.builder()
                .size(BallSize.TEN)
                .gripType("FINGER")
                .color("Red")
                .status(BallStatus.AVAILABLE)
                .build();
    }

    @BeforeAll
    void setupServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();

        WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        bowlingBallClient = new BowlingBallClient(client);
    }

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();
    }

    @AfterAll
    void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void whenCreateBall_thenReturnResponse() throws Exception {
        BowlingBallResponseDTO mockResponse = new BowlingBallResponseDTO("id123", BallSize.TEN, "FINGER", "Red", BallStatus.AVAILABLE);

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockResponse))
                .addHeader("Content-Type", "application/json"));

        BowlingBallRequestDTO request = new BowlingBallRequestDTO(BallSize.TEN, "FINGER", "Red", BallStatus.AVAILABLE);

        BowlingBallResponseDTO result = bowlingBallClient.createBall(request);

        assertThat(result.getId()).isEqualTo("id123");
        assertThat(result.getStatus()).isEqualTo(BallStatus.AVAILABLE);
    }

    @Test
    void whenGetAll_thenReturnList() throws Exception {
        List<BowlingBallResponseDTO> mockList = List.of(
                new BowlingBallResponseDTO("1", BallSize.SIX, "CLAW", "Blue", BallStatus.AVAILABLE),
                new BowlingBallResponseDTO("2", BallSize.TEN, "FINGER", "Red", BallStatus.IN_USE)
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockList))
                .addHeader("Content-Type", "application/json"));

        List<BowlingBallResponseDTO> result = bowlingBallClient.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getColor()).isEqualTo("Blue");
    }

    @Test
    void whenGetBallByIdAndNotFound_thenThrowsNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bowlingBallClient.getBall(validId);
        });

        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
    void whenUpdateBall_thenReturnsResponse() throws Exception {
        BowlingBallResponseDTO updated = new BowlingBallResponseDTO(validId, BallSize.SIX, "HOOK", "Green", BallStatus.IN_USE);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new ObjectMapper().writeValueAsString(updated))
                .addHeader("Content-Type", "application/json"));

        BowlingBallRequestDTO updateRequest = new BowlingBallRequestDTO(BallSize.SIX, "HOOK", "Green", BallStatus.IN_USE);

        BowlingBallResponseDTO result = bowlingBallClient.updateBall(validId, updateRequest);

        assertThat(result.getColor()).isEqualTo("Green");
        assertThat(result.getGripType()).isEqualTo("HOOK");
    }

    @Test
    void whenDeleteBall_thenNoExceptionThrown() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204)); // No content

        assertThatCode(() -> bowlingBallClient.deleteBall(validId))
                .doesNotThrowAnyException();
    }

    @Test
    void whenDeleteBallAndNotFound_thenThrowsNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> bowlingBallClient.deleteBall(validId));
    }

    @Test
    void whenCreateBallAndUnprocessableEntity_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(422)
                .setBody("Validation failed"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                bowlingBallClient.createBall(buildRequest()));

        assertThat(exception.getMessage()).contains("Bowling Ball: Validation failed");
    }

    @Test
    void whenUpdateBallAndBadRequest_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad update input"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                bowlingBallClient.updateBall(validId, buildRequest()));

        assertThat(exception.getMessage()).contains("Bowling Ball: Bad update input");
    }

    @Test
    void whenCreateBallFailsWith5xx_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Server exploded"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                bowlingBallClient.createBall(buildRequest()));

        assertThat(exception.getMessage()).contains("Downstream error");
    }

    @Test
    void whenGetBallFailsWith5xx_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Server failure during GET"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                bowlingBallClient.getBall(validId));

        assertThat(exception.getMessage()).contains("Downstream error");
    }


}
