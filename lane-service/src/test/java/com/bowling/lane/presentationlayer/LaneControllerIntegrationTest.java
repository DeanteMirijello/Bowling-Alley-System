package com.bowling.lane.presentationlayer;

import com.bowling.lane.dataaccesslayer.LaneStatus;
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
public class LaneControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String BASE_URL = "/lanes";
    private final String VALID_ID = "bfe16e5f-c8a6-4f67-82b1-d2044da3fdf6";
    private final String INVALID_ID = "00000000-0000-0000-0000-000000000000";

    @Test
    void whenLanesExist_thenReturnAll() {
        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LaneResponseDTO.class)
                .value(lanes -> assertThat(lanes).hasSize(5));
    }

    @Test
    void whenValidId_thenReturnLane() {
        webTestClient.get()
                .uri(BASE_URL + "/" + VALID_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LaneResponseDTO.class)
                .value(lane -> {
                    assertThat(lane).isNotNull();
                    assertThat(lane.getId()).isEqualTo(VALID_ID);
                });
    }

    @Test
    void whenInvalidId_thenReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Lane not found with ID: " + INVALID_ID);
    }

    @Test
    void whenCreateValidLane_thenLaneIsCreated() {
        LaneRequestDTO request = LaneRequestDTO.builder()
                .laneNumber(6)
                .zone("Z")
                .status(LaneStatus.AVAILABLE)
                .build();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LaneResponseDTO.class)
                .value(created -> {
                    assertThat(created.getId()).isNotBlank();
                    assertThat(created.getLaneNumber()).isEqualTo(6);
                    assertThat(created.getZone()).isEqualTo("Z");
                });
    }

    @Test
    void whenUpdateLane_thenLaneIsUpdated() {
        LaneRequestDTO update = LaneRequestDTO.builder()
                .laneNumber(7)
                .zone("Y")
                .status(LaneStatus.MAINTENANCE)
                .build();

        webTestClient.put()
                .uri(BASE_URL + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LaneResponseDTO.class)
                .value(updated -> {
                    assertThat(updated.getLaneNumber()).isEqualTo(7);
                    assertThat(updated.getZone()).isEqualTo("Y");
                    assertThat(updated.getStatus()).isEqualTo(LaneStatus.MAINTENANCE);
                });
    }

    @Test
    void whenUpdateNonexistentLane_thenReturnNotFound() {
        LaneRequestDTO update = LaneRequestDTO.builder()
                .laneNumber(9)
                .zone("X")
                .status(LaneStatus.MAINTENANCE)
                .build();

        webTestClient.put()
                .uri(BASE_URL + "/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Lane not found with ID: " + INVALID_ID);
    }

    @Test
    void whenDeleteLane_thenLaneIsRemoved() {
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
    void whenDeleteNonexistentLane_thenReturnNotFound() {
        webTestClient.delete()
                .uri(BASE_URL + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Lane not found with ID: " + INVALID_ID);
    }

    @Test
    void whenCreateWithInvalidLaneStatus_thenReturnBadRequest() {
        String invalidRequestJson = """
        {
            "laneNumber": 10,
            "zone": "A",
            "status": "CLOSED"
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
                        ("Invalid lane status. Must be one of: AVAILABLE, IN_USE, MAINTENANCE.");
    }

    @Test
    void whenCreateWithValidLaneStatus_thenReturnCreated() {
        String validRequestJson = """
        {
            "laneNumber": 12,
            "zone": "B",
            "status": "IN_USE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LaneResponseDTO.class)
                .value(created -> {
                    assertThat(created.getLaneNumber()).isEqualTo(12);
                    assertThat(created.getZone()).isEqualTo("B");
                    assertThat(created.getStatus()).isEqualTo(LaneStatus.IN_USE);
                });
    }

    @Test
    void whenCreateLaneWithMissingZone_thenReturnBadRequest() {
        String invalidJson = """
        {
            "laneNumber": 4,
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Test
    void whenPostWithMalformedJson_thenReturnBadRequest() {
        String malformedJson = "{ laneNumber: 5, ";

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(malformedJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid request body.");
    }

    @Test
    void whenPostWithValidJson_thenMapperToEntityIsUsed() {
        String requestJson = """
        {
            "laneNumber": 21,
            "zone": "W",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LaneResponseDTO.class)
                .value(dto -> {
                    assertThat(dto.getLaneNumber()).isEqualTo(21);
                    assertThat(dto.getZone()).isEqualTo("W");
                    assertThat(dto.getStatus()).isEqualTo(LaneStatus.AVAILABLE);
                });
    }

    @Test
    void whenPostLaneWithValidJson_thenMapperToEntityIsUsed() {
        String requestJson = """
        {
            "laneNumber": 22,
            "zone": "U",
            "status": "IN_USE"
        }
        """;

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LaneResponseDTO.class)
                .value(dto -> {
                    assertThat(dto.getLaneNumber()).isEqualTo(22);
                    assertThat(dto.getZone()).isEqualTo("U");
                    assertThat(dto.getStatus()).isEqualTo(LaneStatus.IN_USE);
                });
    }
}
