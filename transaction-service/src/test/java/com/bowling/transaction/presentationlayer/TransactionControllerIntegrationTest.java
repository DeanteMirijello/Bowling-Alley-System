package com.bowling.transaction.presentationlayer;

import com.bowling.transaction.dataaccesslayer.Transaction;
import com.bowling.transaction.dataaccesslayer.TransactionIdentifier;
import com.bowling.transaction.dataaccesslayer.TransactionRepository;
import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import com.bowling.transaction.domainclientlayer.bowlingball.*;
import com.bowling.transaction.domainclientlayer.lane.LaneModel;
import com.bowling.transaction.domainclientlayer.lane.LaneServiceClient;
import com.bowling.transaction.domainclientlayer.lane.LaneStatus;
import com.bowling.transaction.domainclientlayer.shoe.ShoeModel;
import com.bowling.transaction.domainclientlayer.shoe.ShoeServiceClient;
import com.bowling.transaction.domainclientlayer.shoe.ShoeStatus;
import com.bowling.transaction.domainclientlayer.shoe.ShoeSize;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private LaneServiceClient laneServiceClient;

    @MockitoBean
    private BowlingBallServiceClient bowlingBallServiceClient;

    @MockitoBean
    private ShoeServiceClient shoeServiceClient;

    private final String BASE_URL = "/api/transactions";
    private String validId;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();

        Mockito.when(laneServiceClient.getLaneByLaneId(anyString()))
                .thenReturn(LaneModel.builder()
                        .id(UUID.randomUUID().toString())
                        .laneNumber(5)
                        .zone("ZONE_1")
                        .status(LaneStatus.AVAILABLE)
                        .build());

        Mockito.when(bowlingBallServiceClient.getBowlingBallById(anyString()))
                .thenReturn(BowlingBallModel.builder()
                        .id(UUID.randomUUID().toString())
                        .size(BallSize.TEN)
                        .gripType("FINGER")
                        .color("Red")
                        .status(BallStatus.IN_USE)
                        .build());

        Mockito.when(shoeServiceClient.getShoeById(anyString()))
                .thenReturn(ShoeModel.builder()
                        .id(UUID.randomUUID().toString())
                        .size(ShoeSize.SIZE_9)
                        .purchaseDate(LocalDate.now().minusMonths(2))
                        .status(ShoeStatus.AVAILABLE)
                        .build());

        Transaction transaction = Transaction.builder()
                .transactionIdentifier(TransactionIdentifier.generate())
                .customerName("Test User")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .laneZone("ZONE_1")
                .status(TransactionStatus.OPEN)
                .totalPrice(BigDecimal.valueOf(30))
                .dateCompleted(LocalDate.now().toString())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        validId = saved.getTransactionIdentifier().getId();
    }

    @Test
    void whenGetAll_thenReturnList() {
        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseDTO.class)
                .value(transactions -> assertThat(transactions).isNotEmpty());
    }

    @Test
    void whenGetByValidId_thenReturnTransaction() {
        webTestClient.get().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDTO.class)
                .value(response -> assertThat(response.getTransactionId()).isEqualTo(validId));
    }

    @Test
    void whenGetByInvalidId_thenReturnNotFound() {
        String invalidId = UUID.randomUUID().toString();

        webTestClient.get().uri(BASE_URL + "/" + invalidId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateValidTransactionStatus_thenReturnCreated() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .customerName("Create User")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .status(TransactionStatus.OPEN)
                .build();

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDTO.class)
                .value(response -> {
                    assertThat(response.getCustomerName()).isEqualTo("Create User");
                    assertThat(response.getTransactionId()).isNotBlank();
                });
    }

    @Test
    void whenUpdateWithValidId_thenReturnUpdatedTransaction() {
        TransactionRequestDTO update = TransactionRequestDTO.builder()
                .customerName("Updated User")
                .laneId(UUID.randomUUID().toString())
                .bowlingBallId(UUID.randomUUID().toString())
                .shoeId(UUID.randomUUID().toString())
                .status(TransactionStatus.COMPLETED)
                .build();

        webTestClient.put().uri(BASE_URL + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDTO.class)
                .value(updated -> assertThat(updated.getCustomerName()).isEqualTo("Updated User"));
    }

    @Test
    void whenDeleteValidId_thenReturnNoContent() {
        webTestClient.delete().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenDeleteInvalidId_thenReturnNotFound() {
        String invalidId = UUID.randomUUID().toString();

        webTestClient.delete().uri(BASE_URL + "/" + invalidId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenPostWithInvalidEnum_thenReturnBadRequest() {
        String invalidRequest = """
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
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenPostWithMissingField_thenReturnBadRequest() {
        String invalidRequest = """
        {
            "laneId": "123",
            "bowlingBallId": "456",
            "shoeId": "789",
            "status": "OPEN"
        }
        """;

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}