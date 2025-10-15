package com.bowling.transaction.businesslayer;

import static org.junit.jupiter.api.Assertions.*;

import com.bowling.transaction.dataaccesslayer.Transaction;
import com.bowling.transaction.dataaccesslayer.TransactionIdentifier;
import com.bowling.transaction.dataaccesslayer.TransactionRepository;
import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import com.bowling.transaction.domainclientlayer.bowlingball.BowlingBallServiceClient;
import com.bowling.transaction.domainclientlayer.lane.LaneModel;
import com.bowling.transaction.domainclientlayer.lane.LaneServiceClient;
import com.bowling.transaction.domainclientlayer.lane.LaneStatus;
import com.bowling.transaction.domainclientlayer.shoe.ShoeServiceClient;
import com.bowling.transaction.exceptionlayer.InvalidInputException;
import com.bowling.transaction.exceptionlayer.InvalidTransactionStatusException;
import com.bowling.transaction.exceptionlayer.NotFoundException;
import com.bowling.transaction.mappinglayer.TransactionMapper;
import com.bowling.transaction.presentationlayer.TransactionRequestDTO;
import com.bowling.transaction.presentationlayer.TransactionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TransactionServiceImplUnitTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private LaneServiceClient laneServiceClient;

    @Mock
    private BowlingBallServiceClient bowlingBallServiceClient;

    @Mock
    private ShoeServiceClient shoeServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TransactionRequestDTO buildValidRequest() {
        return TransactionRequestDTO.builder()
                .customerName("User A")
                .laneId("lane-123")
                .bowlingBallId("ball-456")
                .shoeId("shoe-789")
                .status(TransactionStatus.OPEN)
                .build();
    }

    @Test
    void whenGetAllTransactions_thenReturnList() {
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2));
        when(transactionMapper.toResponseDTO(any())).thenReturn(new TransactionResponseDTO());

        List<TransactionResponseDTO> result = transactionService.getAllTransactions();
        assertEquals(2, result.size());
    }

    @Test
    void whenGetTransactionByIdExists_thenReturnDTO() {
        String tid = UUID.randomUUID().toString();
        Transaction tx = new Transaction();
        TransactionResponseDTO dto = new TransactionResponseDTO();

        when(transactionRepository.findByTransactionIdentifier_TransactionId(tid)).thenReturn(tx);
        when(transactionMapper.toResponseDTO(tx)).thenReturn(dto);

        TransactionResponseDTO result = transactionService.getTransactionById(tid);
        assertEquals(dto, result);
    }

    @Test
    void whenGetTransactionByIdMissing_thenThrow() {
        when(transactionRepository.findByTransactionIdentifier_TransactionId(anyString()))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            transactionService.getTransactionById("missing-id");
        });
    }

    @Test
    void whenCreateTransactionWithNullStatus_thenThrow() {
        TransactionRequestDTO invalid = buildValidRequest();
        invalid.setStatus(null);

        assertThrows(InvalidTransactionStatusException.class, () -> {
            transactionService.createTransaction(invalid);
        });
    }

    @Test
    void whenCreateTransactionAndLaneIsUnavailable_thenThrow() {
        TransactionRequestDTO request = buildValidRequest();
        request.setStatus(TransactionStatus.COMPLETED);

        when(laneServiceClient.getLaneByLaneId(anyString()))
                .thenReturn(LaneModel.builder().status(LaneStatus.IN_USE).build());

        assertThrows(InvalidInputException.class, () -> {
            transactionService.createTransaction(request);
        });
    }

    @Test
    void whenCreateTransactionValid_thenReturnDTO() {
        TransactionRequestDTO request = buildValidRequest();
        Transaction entity = new Transaction();
        Transaction saved = new Transaction();
        TransactionResponseDTO response = new TransactionResponseDTO();

        when(laneServiceClient.getLaneByLaneId(anyString()))
                .thenReturn(LaneModel.builder().zone("ZONE_1").status(LaneStatus.AVAILABLE).build());
        when(transactionMapper.toEntity(request)).thenReturn(entity);
        when(transactionRepository.save(any())).thenReturn(saved);
        when(transactionMapper.toResponseDTO(saved)).thenReturn(response);

        TransactionResponseDTO result = transactionService.createTransaction(request);
        assertEquals(response, result);
    }

    @Test
    void whenUpdateTransactionMissing_thenThrow() {
        when(transactionRepository.findByTransactionIdentifier_TransactionId(anyString()))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            transactionService.updateTransaction("missing-id", buildValidRequest());
        });
    }

    @Test
    void whenUpdateTransactionNullStatus_thenThrow() {
        TransactionRequestDTO request = buildValidRequest();
        request.setStatus(null);

        assertThrows(InvalidTransactionStatusException.class, () -> {
            transactionService.updateTransaction("any-id", request);
        });
    }

    @Test
    void whenUpdateTransactionValid_thenReturnDTO() {
        TransactionRequestDTO request = buildValidRequest();
        request.setStatus(TransactionStatus.OPEN);

        Transaction existing = new Transaction();
        existing.setTransactionIdentifier(TransactionIdentifier.generate());
        existing.setTotalPrice(BigDecimal.valueOf(30));
        existing.setDateCompleted(LocalDate.now().toString());
        existing.setId("db-id");

        Transaction entity = new Transaction();
        Transaction saved = new Transaction();
        TransactionResponseDTO response = new TransactionResponseDTO();

        when(transactionRepository.findByTransactionIdentifier_TransactionId(anyString()))
                .thenReturn(existing);
        when(laneServiceClient.getLaneByLaneId(anyString()))
                .thenReturn(LaneModel.builder().zone("ZONE_1").status(LaneStatus.AVAILABLE).build());
        when(transactionMapper.toEntity(request)).thenReturn(entity);
        when(transactionRepository.save(any())).thenReturn(saved);
        when(transactionMapper.toResponseDTO(saved)).thenReturn(response);

        TransactionResponseDTO result = transactionService.updateTransaction("valid-id", request);
        assertEquals(response, result);
    }

    @Test
    void whenDeleteTransactionMissing_thenThrow() {
        when(transactionRepository.findByTransactionIdentifier_TransactionId(anyString()))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            transactionService.deleteTransaction("bad-id");
        });
    }

    @Test
    void whenDeleteTransactionValid_thenDeleteSuccessfully() {
        Transaction existing = new Transaction();
        when(transactionRepository.findByTransactionIdentifier_TransactionId(anyString()))
                .thenReturn(existing);

        transactionService.deleteTransaction("some-id");

        verify(transactionRepository, times(1)).delete(existing);
    }
}