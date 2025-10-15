package com.bowling.apigateway.transaction.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.transaction.business.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionApiGatewayControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TransactionService transactionService;

    private final String BASE_URL = "/api/transactions";

    private TransactionRequestDTO validRequest;
    private TransactionResponseDTO validResponse;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        validRequest = TransactionRequestDTO.builder()
                .customerName("Gateway User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .status(TransactionStatus.OPEN)
                .build();

        validResponse = TransactionResponseDTO.builder()
                .transactionId(validId)
                .customerName("Gateway User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .laneZone("ZONE_1")
                .status(TransactionStatus.OPEN)
                .totalPrice(BigDecimal.valueOf(30))
                .dateCompleted(LocalDate.now().toString())
                .build();
    }

    @Test
    void whenGetAll_thenReturnList() {
        Mockito.when(transactionService.getAllTransactions())
                .thenReturn(List.of(validResponse));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("_embedded.transactionResponseDTOList[0].transactionId").isEqualTo(validId);
    }

    @Test
    void whenGetByValidId_thenReturnTransaction() {
        Mockito.when(transactionService.getTransactionById(validId))
                .thenReturn(validResponse);

        webTestClient.get().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("transactionId").isEqualTo(validId);
    }

    @Test
    void whenGetByInvalidId_thenReturnNotFound() {
        String invalidId = "non-existent-id";
        Mockito.when(transactionService.getTransactionById(invalidId))
                .thenThrow(new NotFoundException("Not found"));

        webTestClient.get().uri(BASE_URL + "/" + invalidId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Not found");
    }

    @Test
    void whenCreateValidTransaction_thenReturnCreated() {
        Mockito.when(transactionService.createTransaction(validRequest))
                .thenReturn(validResponse);

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("transactionId").isEqualTo(validId);
    }

    @Test
    void whenPostWithInvalidEnum_thenReturnBadRequest() {
        String invalidPayload = """
        {
          "customerName": "Invalid Enum",
          "laneId": "123",
          "bowlingBallId": "456",
          "shoeId": "789",
          "status": "WRONG"
        }
        """;

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidPayload)
                .exchange()
                .expectStatus().isBadRequest();
    }

//    @Test
//    void whenPostWithMissingField_thenReturnBadRequest() {
//        TransactionRequestDTO request = TransactionRequestDTO.builder()
//                .laneId("123")
//                .bowlingBallId("456")
//                .shoeId("789")
//                .status(TransactionStatus.OPEN)
//                .build();
//
//        webTestClient.post().uri(BASE_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.message").exists();
//    }


    @Test
    void whenUpdateValidTransaction_thenReturnUpdated() {
        Mockito.when(transactionService.updateTransaction(eq(validId), any()))
                .thenReturn(validResponse);

        webTestClient.put().uri(BASE_URL + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("transactionId").isEqualTo(validId);
    }

    @Test
    void whenDeleteValidId_thenReturnNoContent() {
        webTestClient.delete().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenDeleteInvalidId_thenReturnNotFound() {
        String invalidId = "bad-id";
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(transactionService).deleteTransaction(invalidId);

        webTestClient.delete().uri(BASE_URL + "/" + invalidId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Not found");
    }
}
