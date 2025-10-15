package com.bowling.apigateway.bowlingball.presentation;

import com.bowling.apigateway.bowlingball.business.BowlingBallService;
import com.bowling.apigateway.exceptions.NotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.UUID;



@WebMvcTest(BowlingBallController.class)
@ActiveProfiles("test")
class BowlingBallApiGatewayControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BowlingBallService bowlingBallService;

    private String baseUrl = "/api/balls";
    private String validId;
    private BowlingBallRequestDTO request;
    private BowlingBallResponseDTO response;

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
    void whenGetAll_thenReturns200() throws Exception {
        CollectionModel<EntityModel<BowlingBallResponseDTO>> model = CollectionModel.of(List.of(EntityModel.of(response)));

        Mockito.when(bowlingBallService.getAll()).thenReturn(model);

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.bowlingBallResponseDTOList[0].id").value(validId));
    }

    @Test
    void whenGetById_thenReturns200() throws Exception {
        Mockito.when(bowlingBallService.getById(validId))
                .thenReturn(EntityModel.of(response));

        mockMvc.perform(get(baseUrl + "/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenGetInvalidId_thenReturns404() throws Exception {
        Mockito.when(bowlingBallService.getById("bad-id"))
                .thenThrow(new NotFoundException("Bowling ball not found"));

        mockMvc.perform(get(baseUrl + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Bowling ball not found"));
    }

    @Test
    void whenCreateValid_thenReturns201() throws Exception {
        Mockito.when(bowlingBallService.create(request)).thenReturn(EntityModel.of(response));

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenCreateInvalidEnum_thenReturns400() throws Exception {
        String badPayload = """
        {
            "size": "TWENTY",
            "gripType": "FINGER",
            "color": "Red",
            "status": "AVAILABLE"
        }
        """;

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateValid_thenReturns200() throws Exception {
        Mockito.when(bowlingBallService.update(eq(validId), any()))
                .thenReturn(EntityModel.of(response));

        mockMvc.perform(put(baseUrl + "/" + validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(validId));
    }

    @Test
    void whenDeleteValid_thenReturns204() throws Exception {
        mockMvc.perform(delete(baseUrl + "/" + validId))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteInvalid_thenReturns404() throws Exception {
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(bowlingBallService).delete("bad-id");

        mockMvc.perform(delete(baseUrl + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Not found"));
    }
}
