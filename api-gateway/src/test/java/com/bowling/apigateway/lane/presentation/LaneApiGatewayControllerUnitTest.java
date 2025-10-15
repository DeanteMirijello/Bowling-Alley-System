package com.bowling.apigateway.lane.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.lane.business.LaneService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaneController.class)
@ActiveProfiles("test")
class LaneApiGatewayControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .laneNumber(9)
                .zone("A")
                .status(LaneStatus.AVAILABLE)
                .build();

        response = LaneResponseDTO.builder()
                .id(validId)
                .laneNumber(9)
                .zone("A")
                .status(LaneStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGetAll_thenReturns200() throws Exception {
        CollectionModel<EntityModel<LaneResponseDTO>> model = CollectionModel.of(List.of(EntityModel.of(response)));

        Mockito.when(laneService.getAll()).thenReturn(model);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.laneResponseDTOList[0].id").value(validId));
    }

    @Test
    void whenGetById_thenReturns200() throws Exception {
        Mockito.when(laneService.getById(validId)).thenReturn(EntityModel.of(response));

        mockMvc.perform(get(BASE_URL + "/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenGetInvalidId_thenReturns404() throws Exception {
        Mockito.when(laneService.getById("bad-id"))
                .thenThrow(new NotFoundException("Lane not found"));

        mockMvc.perform(get(BASE_URL + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Lane not found"));
    }

    @Test
    void whenCreateValid_thenReturns201() throws Exception {
        Mockito.when(laneService.create(request)).thenReturn(EntityModel.of(response));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenCreateInvalidEnum_thenReturns400() throws Exception {
        String invalidPayload = """
        {
            "laneNumber": 2,
            "zone": "Z",
            "status": "BROKEN"
        }
        """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateValid_thenReturns200() throws Exception {
        Mockito.when(laneService.update(eq(validId), any()))
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
        Mockito.doThrow(new NotFoundException("Lane not found"))
                .when(laneService).delete("bad-id");

        mockMvc.perform(delete(BASE_URL + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Lane not found"));
    }
}
