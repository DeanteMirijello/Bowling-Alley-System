package com.bowling.apigateway.transaction.domainclient;

import com.bowling.apigateway.exceptions.InvalidInputException;
import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.transaction.presentation.TransactionRequestDTO;
import com.bowling.apigateway.transaction.presentation.TransactionResponseDTO;
import com.bowling.apigateway.transaction.presentation.TransactionStatus;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionClientIntegrationTest {

    private MockWebServer mockWebServer;
    private TransactionClient transactionClient;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String validId = UUID.randomUUID().toString();

    @BeforeAll
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient client = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        transactionClient = new TransactionClient(client);
    }

    @AfterAll
    void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    private TransactionRequestDTO buildRequest() {
        return TransactionRequestDTO.builder()
                .customerName("Test User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .status(TransactionStatus.OPEN)
                .build();
    }

    private TransactionResponseDTO buildResponse(String id) {
        return TransactionResponseDTO.builder()
                .transactionId(id)
                .customerName("Test User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .laneZone("B")
                .status(TransactionStatus.OPEN)
                .totalPrice(BigDecimal.valueOf(30))
                .dateCompleted(LocalDate.now().toString())
                .build();
    }

    @Test
    void whenCreate_thenReturnsResponse() throws Exception {
        TransactionResponseDTO mock = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mock))
                .addHeader("Content-Type", "application/json"));

        TransactionResponseDTO result = transactionClient.createTransaction(buildRequest());

        assertThat(result.getTransactionId()).isEqualTo(validId);
    }

    @Test
    void whenGetAll_thenReturnsList() throws Exception {
        List<TransactionResponseDTO> mockList = List.of(buildResponse("1"), buildResponse("2"));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mockList))
                .addHeader("Content-Type", "application/json"));

        List<TransactionResponseDTO> result = transactionClient.getAllTransactions();

        assertThat(result).hasSize(2);
    }

    @Test
    void whenGetById_thenReturnsResponse() throws Exception {
        TransactionResponseDTO mock = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(mock))
                .addHeader("Content-Type", "application/json"));

        TransactionResponseDTO result = transactionClient.getTransactionById(validId);

        assertThat(result.getCustomerName()).isEqualTo("Test User");
    }

    @Test
    void whenGetByInvalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> transactionClient.getTransactionById("bad-id"));
    }

    @Test
    void whenGetByIdNotFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> transactionClient.getTransactionById(validId));
    }

    @Test
    void whenUpdate_thenReturnsResponse() throws Exception {
        TransactionResponseDTO updated = buildResponse(validId);

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(updated))
                .addHeader("Content-Type", "application/json"));

        TransactionResponseDTO result = transactionClient.updateTransaction(validId, buildRequest());

        assertThat(result.getTransactionId()).isEqualTo(validId);
    }

    @Test
    void whenUpdateWithInvalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () ->
                transactionClient.updateTransaction("bad-id", buildRequest()));
    }

    @Test
    void whenDelete_thenNoException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        assertThatCode(() -> transactionClient.deleteTransaction(validId)).doesNotThrowAnyException();
    }

    @Test
    void whenDeleteInvalidUUID_thenThrows() {
        assertThrows(InvalidInputException.class, () -> transactionClient.deleteTransaction("bad-id"));
    }

    @Test
    void whenDeleteNotFound_thenThrows() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () -> transactionClient.deleteTransaction(validId));
    }

    @Test
    void whenCreateFailsWith422_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(422)
                .setBody("Invalid transaction payload"));

        assertThrows(InvalidInputException.class, () ->
                transactionClient.createTransaction(buildRequest()));
    }

    @Test
    void whenUpdateFailsWith400_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad update"));

        assertThrows(InvalidInputException.class, () ->
                transactionClient.updateTransaction(validId, buildRequest()));
    }

    @Test
    void whenGetFailsWith500_thenThrowsInvalidInputException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal error"));

        assertThrows(InvalidInputException.class, () ->
                transactionClient.getTransactionById(validId));
    }

    @Test
    void whenDeleteReturns404_thenThrowsNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(NotFoundException.class, () ->
                transactionClient.deleteTransaction(validId));
    }

}
