package com.bowling.apigateway.lane.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.lane.business.LaneService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.UUID;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LaneApiGatewayControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LaneService laneService;

    private final String BASE_URL = "/api/lanes";

    private LaneRequestDTO request;
    private LaneResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = LaneRequestDTO.builder()
                .laneNumber(5)
                .zone("B")
                .status(LaneStatus.AVAILABLE)
                .build();

        response = LaneResponseDTO.builder()
                .id(validId)
                .laneNumber(5)
                .zone("B")
                .status(LaneStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGetAll_thenReturnsList() {
        Mockito.when(laneService.getAll())
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(response))));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("_embedded.laneResponseDTOList[0].id").isEqualTo(validId);
    }

    @Test
    void whenGetById_thenReturnsEntity() {
        Mockito.when(laneService.getById(validId))
                .thenReturn(EntityModel.of(response));

        webTestClient.get().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(validId);
    }

    @Test
    void whenGetInvalidId_thenReturns404() {
        Mockito.when(laneService.getById("bad-id"))
                .thenThrow(new NotFoundException("Lane not found"));

        webTestClient.get().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Lane not found");
    }

    @Test
    void whenCreateValid_thenReturns201() {
        Mockito.when(laneService.create(request))
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
    void whenCreateInvalidEnum_thenReturns400() {
        String invalidPayload = """
        {
            "laneNumber": 7,
            "zone": "C",
            "status": "BLOCKED"
        }
        """;

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidPayload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUpdateValid_thenReturns200() {
        Mockito.when(laneService.update(eq(validId), any()))
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
    void whenDeleteValid_thenReturns204() {
        webTestClient.delete().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenDeleteInvalid_thenReturns404() {
        Mockito.doThrow(new NotFoundException("Lane not found"))
                .when(laneService).delete("bad-id");

        webTestClient.delete().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Lane not found");
    }
}
