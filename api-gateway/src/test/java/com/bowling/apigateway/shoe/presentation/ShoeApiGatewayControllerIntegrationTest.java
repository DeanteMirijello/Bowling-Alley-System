package com.bowling.apigateway.shoe.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.shoe.business.ShoeService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShoeApiGatewayControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ShoeService shoeService;

    private final String BASE_URL = "/api/shoes";

    private ShoeRequestDTO request;
    private ShoeResponseDTO response;
    private String validId;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_9)
                .purchaseDate(LocalDate.now().minusMonths(1))
                .status(ShoeStatus.AVAILABLE)
                .build();

        response = ShoeResponseDTO.builder()
                .id(validId)
                .size(ShoeSize.SIZE_9)
                .purchaseDate(request.getPurchaseDate())
                .status(ShoeStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGetAll_thenReturnsList() {
        Mockito.when(shoeService.getAll())
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(response))));

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("_embedded.shoeResponseDTOList[0].id").isEqualTo(validId);
    }

    @Test
    void whenGetById_thenReturnsEntity() {
        Mockito.when(shoeService.getById(validId))
                .thenReturn(EntityModel.of(response));

        webTestClient.get().uri(BASE_URL + "/" + validId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(validId);
    }

    @Test
    void whenGetInvalidId_thenReturns404() {
        Mockito.when(shoeService.getById("bad-id"))
                .thenThrow(new NotFoundException("Shoe not found"));

        webTestClient.get().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Shoe not found");
    }

    @Test
    void whenCreateValid_thenReturns201() {
        Mockito.when(shoeService.create(request))
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
            "size": "SIZE_99",
            "purchaseDate": "2023-12-01",
            "status": "AVAILABLE"
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
        Mockito.when(shoeService.update(eq(validId), any()))
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
        Mockito.doThrow(new NotFoundException("Shoe not found"))
                .when(shoeService).delete("bad-id");

        webTestClient.delete().uri(BASE_URL + "/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("message").isEqualTo("Shoe not found");
    }
}
