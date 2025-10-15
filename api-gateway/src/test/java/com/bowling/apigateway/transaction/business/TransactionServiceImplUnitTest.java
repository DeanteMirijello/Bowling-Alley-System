package com.bowling.apigateway.transaction.business;

import com.bowling.apigateway.transaction.domainclient.TransactionClient;
import com.bowling.apigateway.transaction.presentation.TransactionRequestDTO;
import com.bowling.apigateway.transaction.presentation.TransactionResponseDTO;
import com.bowling.apigateway.transaction.presentation.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplUnitTest {

    @Mock
    private TransactionClient transactionClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private String validId;
    private TransactionRequestDTO request;
    private TransactionResponseDTO response;

    @BeforeEach
    void setup() {
        validId = UUID.randomUUID().toString();

        request = TransactionRequestDTO.builder()
                .customerName("Test Customer")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .status(TransactionStatus.OPEN)
                .build();

        response = TransactionResponseDTO.builder()
                .transactionId(validId)
                .customerName("Test Customer")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .laneZone("A")
                .status(TransactionStatus.OPEN)
                .totalPrice(BigDecimal.valueOf(30))
                .dateCompleted(LocalDate.now().toString())
                .build();
    }

    @Test
    void whenGetAll_thenReturnList() {
        List<TransactionResponseDTO> expected = List.of(response);

        Mockito.when(transactionClient.getAllTransactions()).thenReturn(expected);

        List<TransactionResponseDTO> actual = transactionService.getAllTransactions();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenGetById_thenReturnTransaction() {
        Mockito.when(transactionClient.getTransactionById(validId)).thenReturn(response);

        TransactionResponseDTO actual = transactionService.getTransactionById(validId);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void whenCreate_thenReturnResponse() {
        Mockito.when(transactionClient.createTransaction(request)).thenReturn(response);

        TransactionResponseDTO actual = transactionService.createTransaction(request);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void whenUpdate_thenReturnResponse() {
        Mockito.when(transactionClient.updateTransaction(validId, request)).thenReturn(response);

        TransactionResponseDTO actual = transactionService.updateTransaction(validId, request);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void whenDelete_thenDelegateToClient() {
        transactionService.deleteTransaction(validId);

        Mockito.verify(transactionClient).deleteTransaction(validId);
    }
}
