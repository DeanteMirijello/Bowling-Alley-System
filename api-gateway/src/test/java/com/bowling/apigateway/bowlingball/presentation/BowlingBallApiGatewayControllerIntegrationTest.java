package com.bowling.apigateway.bowlingball.presentation;

import com.bowling.apigateway.bowlingball.business.BowlingBallService;
import com.bowling.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BowlingBallApiGatewayControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BowlingBallService bowlingBallService;

    private final String BASE_URL = "/api/balls";

    private BowlingBallRequestDTO request;
    private BowlingBallResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = BowlingBallRequestDTO.builder()
                .size(BallSize.TEN)
                .gripType("FINGER")
                .color("Red")
                .status(BallStatus.AVAILABLE)
                .build();

        response = BowlingBallResponseDTO.builder()
                .id(validId)
                .size(BallSize.TEN)
                .gripType("FINGER")
                .color("Red")
                .status(BallStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGetAll_thenReturnList() {
        Mockito.when(bowlingBallService.getAll())
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(response))));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("_embedded.bowlingBallResponseDTOList[0].id").isEqualTo(validId);
    }

    @Test
    void whenGetById_thenReturnEntity() {
        Mockito.when(bowlingBallService.getById(validId))
                .thenReturn(EntityModel.of(response));

        webTestClient.get().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(validId);
    }

    @Test
    void whenGetInvalidId_thenReturn404() {
        Mockito.when(bowlingBallService.getById("bad-id"))
                .thenThrow(new NotFoundException("Bowling ball not found"));

        webTestClient.get().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Bowling ball not found");
    }

    @Test
    void whenCreateValid_thenReturnCreated() {
        Mockito.when(bowlingBallService.create(request))
                .thenReturn(EntityModel.of(response));

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("id").isEqualTo(validId);
    }

    @Test
    void whenCreateWithInvalidEnum_thenReturn400() {
        String payload = """
        {
            "size": "TWENTY",
            "gripType": "FINGER",
            "color": "Red",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUpdateValid_thenReturnUpdated() {
        Mockito.when(bowlingBallService.update(eq(validId), any()))
                .thenReturn(EntityModel.of(response));

        webTestClient.put().uri(BASE_URL + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(validId);
    }

    @Test
    void whenDeleteValidId_thenReturn204() {
        webTestClient.delete().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenDeleteInvalidId_thenReturn404() {
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(bowlingBallService).delete("bad-id");

        webTestClient.delete().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Not found");
    }
}
