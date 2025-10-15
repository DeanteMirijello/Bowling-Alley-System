package com.bowling.shoe.presentationlayer;

import com.bowling.shoe.dataaccesslayer.ShoeSize;
import com.bowling.shoe.dataaccesslayer.ShoeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Sql({"/schema-h2.sql", "/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ShoeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String VALID_ID = "348d0348-6d25-4b66-b392-db367465d0dc";
    private final String INVALID_ID = "00000000-0000-0000-0000-000000000000";

    @Test
    void whenShoesExist_thenReturnAllShoes() {
        webTestClient.get()
                .uri("/shoes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ShoeResponseDTO.class)
                .value(shoes -> assertThat(shoes).hasSize(5));
    }

    @Test
    void whenValidShoeId_thenReturnShoe() {
        webTestClient.get()
                .uri("/shoes/{id}", VALID_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShoeResponseDTO.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(VALID_ID);
                });
    }

    @Test
    void whenInvalidShoeId_thenReturnNotFound() {
        webTestClient.get()
                .uri("/shoes/{id}", INVALID_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Shoe not found with ID: " + INVALID_ID);
    }

    @Test
    void whenCreateValidShoe_thenShoeIsCreated() {
        ShoeRequestDTO newShoe = ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_10)
                .purchaseDate(LocalDate.of(2024, 4, 10))
                .status(ShoeStatus.AVAILABLE)
                .build();

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newShoe)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ShoeResponseDTO.class)
                .value(created -> {
                    assertThat(created.getId()).isNotBlank();
                    assertThat(created.getSize()).isEqualTo(newShoe.getSize());
                    assertThat(created.getStatus()).isEqualTo(newShoe.getStatus());
                });
    }

    @Test
    void whenCreateWithValidShoeSize_thenReturnCreated() {
        String validRequestJson = """
        {
            "size": "SIZE_7",
            "purchaseDate": "2024-04-10",
            "status": "IN_USE"
        }
        """;

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ShoeResponseDTO.class)
                .value(created -> {
                    assertThat(created.getSize()).isEqualTo(ShoeSize.SIZE_7);
                    assertThat(created.getStatus()).isEqualTo(ShoeStatus.IN_USE);
                });
    }

    @Test
    void whenCreateWithInvalidShoeSize_thenReturnBadRequest() {
        String invalidRequestJson = """
        {
            "size": "SIZE_14",
            "purchaseDate": "2024-04-10",
            "status": "AVAILABLE"
        }
        """;

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo
                        ("Invalid shoe size. Must be one of: 5, 6, 7, 8, 9, 10, 11, 12.");
    }

    @Test
    void whenCreateWithInvalidShoeStatus_thenReturnBadRequest() {
        String requestJson = """
        {
            "size": "SIZE_9",
            "purchaseDate": "2024-04-10",
            "status": "BROKEN"
        }
        """;

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(msg ->
                        assertThat((String) msg).contains("Invalid shoe status")
                );
    }

    @Test
    void whenPostWithValidJson_thenMapperToEntityIsUsed() {
        String requestJson = """
        {
            "size": "SIZE_10",
            "purchaseDate": "2024-04-10",
            "status": "IN_USE"
        }
        """;

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ShoeResponseDTO.class)
                .value(dto -> {
                    assertThat(dto.getSize()).isEqualTo(ShoeSize.SIZE_10);
                    assertThat(dto.getStatus()).isEqualTo(ShoeStatus.IN_USE);
                });
    }

    @Test
    void whenPostWithMalformedJson_thenHandledAsBadRequest() {
        String malformedJson = "{ size: \"SIZE_9\", ";

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(malformedJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid request body.");
    }

    @Test
    void whenPostWithUnknownField_thenTriggerGenericHandler() {
        String badJson = """
        {
            "unknownField": "???"
        }
        """;

        webTestClient.post()
                .uri("/shoes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badJson)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Internal server error");
    }

    @Test
    void whenUpdateShoe_thenShoeIsUpdated() {
        ShoeRequestDTO update = ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_8)
                .purchaseDate(LocalDate.of(2022, 12, 1))
                .status(ShoeStatus.IN_USE)
                .build();

        webTestClient.put()
                .uri("/shoes/{id}", VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShoeResponseDTO.class)
                .value(updated -> {
                    assertThat(updated.getSize()).isEqualTo(update.getSize());
                    assertThat(updated.getStatus()).isEqualTo(update.getStatus());
                });
    }

    @Test
    void whenUpdatingNonexistentShoe_thenReturnNotFound() {
        ShoeRequestDTO update = ShoeRequestDTO.builder()
                .size(ShoeSize.SIZE_10)
                .purchaseDate(LocalDate.of(2023, 1, 1))
                .status(ShoeStatus.AVAILABLE)
                .build();

        webTestClient.put()
                .uri("/shoes/" + INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Shoe not found with ID: " + INVALID_ID);
    }

    @Test
    void whenDeleteShoe_thenShoeIsRemoved() {
        webTestClient.delete()
                .uri("/shoes/{id}", VALID_ID)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/shoes/{id}", VALID_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}
