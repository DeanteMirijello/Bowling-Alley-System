package com.bowling.bowlingball.presentationlayer;

import com.bowling.bowlingball.dataaccesslayer.BallSize;
import com.bowling.bowlingball.dataaccesslayer.BallStatus;
import com.bowling.bowlingball.dataaccesslayer.BowlingBall;
import com.bowling.bowlingball.dataaccesslayer.BowlingBallIdentifier;
import com.bowling.bowlingball.mappinglayer.BowlingBallMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Sql({"/schema-h2.sql", "/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BowlingBallControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String BASE_URL = "/bowlingballs";
    private final String VALID_ID = "bb1319f0-6d5f-47a2-9922-3ebec50d49da";
    private final String INVALID_ID = "00000000-0000-0000-0000-000000000000";

    @Test
    void whenBallsExist_thenReturnAll() {
        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BowlingBallResponseDTO.class)
                .value(balls -> assertThat(balls).hasSize(5));
    }

    @Test
    void whenValidId_thenReturnBall() {
        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BowlingBallResponseDTO.class)
                .value(ball -> {
                    assertThat(ball).isNotNull();
                    assertThat(ball.getId()).isEqualTo(VALID_ID);
                });
    }

    @Test
    void whenInvalidId_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bowling ball not found with ID: " + INVALID_ID);
    }

    @Test
    void whenCreateValidBall_thenBallIsCreated() {
        BowlingBallRequestDTO request = BowlingBallRequestDTO.builder()
                .size(BallSize.TEN)
                .gripType("Standard")
                .color("Orange")
                .status(BallStatus.AVAILABLE)
                .build();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BowlingBallResponseDTO.class)
                .value(created -> {
                    assertThat(created.getId()).isNotBlank();
                    assertThat(created.getSize()).isEqualTo(request.getSize());
                    assertThat(created.getColor()).isEqualTo("Orange");
                });
    }

    @Test
    void whenUpdateBall_thenBallIsUpdated() {
        BowlingBallRequestDTO update = BowlingBallRequestDTO.builder()
                .size(BallSize.FOURTEEN)
                .gripType("Contoured")
                .color("Silver")
                .status(BallStatus.IN_USE)
                .build();

        webTestClient.put()
                .uri(BASE_URL + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BowlingBallResponseDTO.class)
                .value(updated -> {
                    assertThat(updated.getSize()).isEqualTo(update.getSize());
                    assertThat(updated.getGripType()).isEqualTo(update.getGripType());
                });
    }

    @Test
    void whenDeleteBall_thenBallIsRemoved() {
        webTestClient.delete()
                .uri(BASE_URL + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUpdateWithInvalidId_thenReturnNotFound() {
        BowlingBallRequestDTO update = BowlingBallRequestDTO.builder()
                .size(BallSize.EIGHT)
                .gripType("Refined")
                .color("Yellow")
                .status(BallStatus.IN_USE)
                .build();

        webTestClient.put()
                .uri(BASE_URL + "/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bowling ball not found with ID: " + INVALID_ID);
    }

    @Test
    void whenDeleteWithInvalidId_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URL + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bowling ball not found with ID: " + INVALID_ID);
    }

    @Test
    void whenUpdateNonexistentBall_thenReturnNotFound() {
        BowlingBallRequestDTO update = BowlingBallRequestDTO.builder()
                .size(BallSize.SIXTEEN)
                .gripType("Deluxe")
                .color("Purple")
                .status(BallStatus.IN_USE)
                .build();

        webTestClient.put()
                .uri(BASE_URL + "/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bowling ball not found with ID: " + INVALID_ID);
    }

    @Test
    void whenDeleteNonexistentBall_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URL + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Bowling ball not found with ID: " + INVALID_ID);
    }

    @Test
    void whenCreateWithMissingGripType_thenReturnBadRequest() {
        String requestJson = """
        {
            "size": "EIGHT",
            "color": "Green",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUpdateWithMissingGripType_thenReturnBadRequest() {
        String updateJson = """
        {
            "size": "TEN",
            "color": "Blue",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.put()
                .uri(BASE_URL + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Test
    void whenUpdateWithMissingColor_thenReturnBadRequest() {
        String updateJson = """
        {
            "size": "EIGHT",
            "gripType": "Standard",
            "status": "IN_USE"
        }
        """;

        webTestClient.put()
                .uri(BASE_URL + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenCreateBall_thenMapperConvertsToResponseDTO() {
        BowlingBallRequestDTO request = BowlingBallRequestDTO.builder()
                .size(BallSize.SIX)
                .gripType("Hybrid")
                .color("Teal")
                .status(BallStatus.AVAILABLE)
                .build();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BowlingBallResponseDTO.class)
                .value(response -> {
                    assertThat(response.getId()).isNotBlank();
                    assertThat(response.getSize()).isEqualTo(request.getSize());
                    assertThat(response.getColor()).isEqualTo(request.getColor());
                    assertThat(response.getGripType()).isEqualTo(request.getGripType());
                });
    }

    @Test
    void whenCreateWithInvalidSize_thenReturnBadRequest() {
        String invalidRequestJson = """
        {
            "size": "TINY",
            "gripType": "Standard",
            "color": "Orange",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo
                        ("Invalid ball size. Must be one of: SIX, EIGHT, TEN, TWELVE, FOURTEEN, SIXTEEN.");
    }

    @Test
    void whenCreateWithValidSize_thenReturnCreated() {
        String validRequestJson = """
        {
            "size": "TEN",
            "gripType": "Standard",
            "color": "Teal",
            "status": "IN_USE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BowlingBallResponseDTO.class)
                .value(created -> {
                    assertThat(created.getSize()).isEqualTo(BallSize.TEN);
                    assertThat(created.getColor()).isEqualTo("Teal");
                });
    }

    @Test
    void whenPostBallWithRawJson_thenMapperToEntityIsUsed() {
        String requestJson = """
        {
            "size": "SIX",
            "gripType": "Hybrid",
            "color": "Gray",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BowlingBallResponseDTO.class)
                .value(dto -> {
                    assertThat(dto.getSize()).isEqualTo(BallSize.SIX);
                    assertThat(dto.getColor()).isEqualTo("Gray");
                });
    }

    @Test
    void whenPostWithInvalidEnum_thenTriggerEnumExceptionHandler() {
        String requestJson = """
        {
            "size": "TINY",
            "gripType": "Standard",
            "color": "Orange",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value((String msg) ->
                        assertThat(msg).isEqualTo("Invalid ball size. Must be one of: SIX, EIGHT, TEN, TWELVE, FOURTEEN, SIXTEEN.")
                );
    }

    @Test
    void whenPostWithMalformedJson_thenHandledAsBadRequest() {
        String invalidJson = "{ size: \"EIGHT\", ";

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(msg ->
                        assertThat(msg).isEqualTo("Invalid request body.")
                );
    }
}
