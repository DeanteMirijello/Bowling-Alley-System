package com.bowling.apigateway.shoe.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.shoe.business.ShoeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoeController.class)
@ActiveProfiles("test")
class ShoeApiGatewayControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .size(ShoeSize.SIZE_10)
                .purchaseDate(LocalDate.now().minusWeeks(2))
                .status(ShoeStatus.AVAILABLE)
                .build();

        response = ShoeResponseDTO.builder()
                .id(validId)
                .size(ShoeSize.SIZE_10)
                .purchaseDate(request.getPurchaseDate())
                .status(ShoeStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGetAll_thenReturns200() throws Exception {
        CollectionModel<EntityModel<ShoeResponseDTO>> model = CollectionModel.of(List.of(EntityModel.of(response)));

        Mockito.when(shoeService.getAll()).thenReturn(model);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.shoeResponseDTOList[0].id").value(validId));
    }

    @Test
    void whenGetById_thenReturns200() throws Exception {
        Mockito.when(shoeService.getById(validId)).thenReturn(EntityModel.of(response));

        mockMvc.perform(get(BASE_URL + "/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenGetInvalidId_thenReturns404() throws Exception {
        Mockito.when(shoeService.getById("bad-id"))
                .thenThrow(new NotFoundException("Shoe not found"));

        mockMvc.perform(get(BASE_URL + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Shoe not found"));
    }

    @Test
    void whenCreateValid_thenReturns201() throws Exception {
        Mockito.when(shoeService.create(request)).thenReturn(EntityModel.of(response));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenCreateInvalidEnum_thenReturns400() throws Exception {
        String badPayload = """
        {
            "size": "SIZE_15",
            "purchaseDate": "2023-01-10",
            "status": "AVAILABLE"
        }
        """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateValid_thenReturns200() throws Exception {
        Mockito.when(shoeService.update(eq(validId), any()))
                .thenReturn(EntityModel.of(response));

        mockMvc.perform(put(BASE_URL + "/" + validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenDeleteValid_thenReturns204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + validId))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteInvalid_thenReturns404() throws Exception {
        Mockito.doThrow(new NotFoundException("Shoe not found"))
                .when(shoeService).delete("bad-id");

        mockMvc.perform(delete(BASE_URL + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Shoe not found"));
    }
}
