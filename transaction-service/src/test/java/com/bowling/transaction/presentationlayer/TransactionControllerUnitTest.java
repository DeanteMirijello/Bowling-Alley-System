package com.bowling.transaction.presentationlayer;

import com.bowling.transaction.businesslayer.TransactionService;
import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenGetAll_thenReturns200() throws Exception {
        Mockito.when(transactionService.getAllTransactions())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetByValidId_thenReturns200() throws Exception {
        Mockito.when(transactionService.getTransactionById(anyString()))
                .thenReturn(TransactionResponseDTO.builder().transactionId(UUID.randomUUID().toString()).build());

        mockMvc.perform(get("/api/transactions/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetByInvalidUUID_thenReturns422() throws Exception {
        mockMvc.perform(get("/api/transactions/invalid-id"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenPostValidTransaction_thenReturns201() throws Exception {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .customerName("Jane Doe")
                .laneId("lane-id")
                .bowlingBallId("ball-id")
                .shoeId("shoe-id")
                .status(TransactionStatus.OPEN)
                .build();

        TransactionResponseDTO response = TransactionResponseDTO.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerName("Jane Doe")
                .build();

        Mockito.when(transactionService.createTransaction(any())).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Jane Doe"));
    }

    @Test
    void whenPostMissingField_thenReturns400() throws Exception {
        String badJson = """
        {
            "laneId": "lane",
            "bowlingBallId": "ball",
            "shoeId": "shoe",
            "status": "OPEN"
        }
        """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPutValidId_thenReturns200() throws Exception {
        TransactionRequestDTO update = TransactionRequestDTO.builder()
                .customerName("Updated")
                .laneId("lane")
                .bowlingBallId("ball")
                .shoeId("shoe")
                .status(TransactionStatus.COMPLETED)
                .build();

        Mockito.when(transactionService.updateTransaction(anyString(), any())).thenReturn(
                TransactionResponseDTO.builder().customerName("Updated").build());

        mockMvc.perform(put("/api/transactions/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Updated"));
    }

    @Test
    void whenPutInvalidId_thenReturns422() throws Exception {
        TransactionRequestDTO update = TransactionRequestDTO.builder()
                .customerName("Updated")
                .laneId("lane")
                .bowlingBallId("ball")
                .shoeId("shoe")
                .status(TransactionStatus.COMPLETED)
                .build();

        mockMvc.perform(put("/api/transactions/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    void whenDeleteValidId_thenReturns204() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteInvalidId_thenReturns422() throws Exception {
        mockMvc.perform(delete("/api/transactions/invalid-id"))
                .andExpect(status().isUnprocessableEntity());
    }
}