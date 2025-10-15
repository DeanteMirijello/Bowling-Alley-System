package com.bowling.apigateway.transaction.presentation;

import com.bowling.apigateway.exceptions.NotFoundException;
import com.bowling.apigateway.transaction.business.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(TransactionController.class)
@ActiveProfiles("test")
class TransactionApiGatewayControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private String baseUrl = "/api/transactions";
    private String validId;
    private TransactionRequestDTO validRequest;
    private TransactionResponseDTO validResponse;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        validRequest = TransactionRequestDTO.builder()
                .customerName("Unit Test User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .status(TransactionStatus.OPEN)
                .build();

        validResponse = TransactionResponseDTO.builder()
                .transactionId(validId)
                .customerName("Unit Test User")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .laneZone("ZONE_1")
                .status(TransactionStatus.OPEN)
                .totalPrice(BigDecimal.valueOf(30))
                .dateCompleted(LocalDate.now().toString())
                .build();
    }

    @Test
    void whenGetAll_thenReturns200() throws Exception {
        Mockito.when(transactionService.getAllTransactions()).thenReturn(List.of(validResponse));

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.transactionResponseDTOList[0].transactionId").value(validId));
    }

    @Test
    void whenGetById_thenReturns200() throws Exception {
        Mockito.when(transactionService.getTransactionById(validId)).thenReturn(validResponse);

        mockMvc.perform(get(baseUrl + "/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").value(validId));
    }

    @Test
    void whenGetByInvalidId_thenReturns404() throws Exception {
        Mockito.when(transactionService.getTransactionById("bad-id"))
                .thenThrow(new NotFoundException("Transaction not found"));

        mockMvc.perform(get(baseUrl + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Transaction not found"));
    }

    @Test
    void whenCreateValid_thenReturns201() throws Exception {
        Mockito.when(transactionService.createTransaction(validRequest)).thenReturn(validResponse);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("transactionId").value(validId));
    }

    @Test
    void whenUpdateValid_thenReturns200() throws Exception {
        Mockito.when(transactionService.updateTransaction(eq(validId), any())).thenReturn(validResponse);

        mockMvc.perform(put(baseUrl + "/" + validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId").value(validId));
    }

    @Test
    void whenDeleteValid_thenReturns204() throws Exception {
        mockMvc.perform(delete(baseUrl + "/" + validId))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteInvalid_thenReturns404() throws Exception {
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(transactionService).deleteTransaction("bad-id");

        mockMvc.perform(delete(baseUrl + "/bad-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Not found"));
    }

//    @Test
//    void whenPostWithMissingField_thenReturns400() throws Exception {
//        String badRequest = """
//        {
//            "laneId": "123",
//            "bowlingBallId": "456",
//            "shoeId": "789",
//            "status": "OPEN"
//        }
//        """;
//
//        mockMvc.perform(post(baseUrl)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(badRequest))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void whenPostWithInvalidEnum_thenReturns400() throws Exception {
        String badRequest = """
        {
            "customerName": "Invalid Enum",
            "laneId": "123",
            "bowlingBallId": "456",
            "shoeId": "789",
            "status": "INVALID"
        }
        """;

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badRequest))
                .andExpect(status().isBadRequest());
    }
}
