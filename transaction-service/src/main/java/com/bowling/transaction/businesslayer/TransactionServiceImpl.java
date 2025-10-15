package com.bowling.transaction.businesslayer;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final LaneServiceClient laneServiceClient;
    private final BowlingBallServiceClient bowlingBallServiceClient;
    private final ShoeServiceClient shoeServiceClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  LaneServiceClient laneServiceClient,
                                  BowlingBallServiceClient bowlingBallServiceClient,
                                  ShoeServiceClient shoeServiceClient) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.laneServiceClient = laneServiceClient;
        this.bowlingBallServiceClient = bowlingBallServiceClient;
        this.shoeServiceClient = shoeServiceClient;
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TransactionResponseDTO getTransactionById(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionIdentifier_TransactionId(transactionId);
        if (transaction == null) {
            throw new NotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) {
        if (requestDTO.getStatus() == null) {
            throw new InvalidTransactionStatusException("Transaction status is required and must be valid.");
        }

        LaneModel lane = validateAndFetchDependencies(requestDTO);

        Transaction transaction = transactionMapper.toEntity(requestDTO);
        transaction.setTransactionIdentifier(TransactionIdentifier.generate());
        transaction.setLaneZone(lane.getZone());
        transaction.setTotalPrice(BigDecimal.valueOf(30));
        transaction.setDateCompleted(LocalDate.now().toString());

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponseDTO(saved);
    }

    @Override
    public TransactionResponseDTO updateTransaction(String transactionId, TransactionRequestDTO requestDTO) {
        if (requestDTO.getStatus() == null) {
            throw new InvalidTransactionStatusException("Transaction status is required and must be valid.");
        }

        Transaction existing = transactionRepository.findByTransactionIdentifier_TransactionId(transactionId);
        if (existing == null) {
            throw new NotFoundException("Transaction with ID " + transactionId + " not found.");
        }

        LaneModel lane = validateAndFetchDependencies(requestDTO);

        Transaction updated = transactionMapper.toEntity(requestDTO);
        updated.setId(existing.getId());
        updated.setTransactionIdentifier(existing.getTransactionIdentifier());
        updated.setTotalPrice(existing.getTotalPrice());
        updated.setDateCompleted(existing.getDateCompleted());
        updated.setLaneZone(lane.getZone());

        return transactionMapper.toResponseDTO(transactionRepository.save(updated));
    }

    @Override
    public void deleteTransaction(String transactionId) {
        Transaction existing = transactionRepository.findByTransactionIdentifier_TransactionId(transactionId);
        if (existing == null) {
            throw new NotFoundException("Transaction with ID " + transactionId + " not found.");
        }
        transactionRepository.delete(existing);
    }

    private LaneModel validateAndFetchDependencies(TransactionRequestDTO requestDTO) {
        LaneModel lane = laneServiceClient.getLaneByLaneId(requestDTO.getLaneId());

        if (requestDTO.getStatus() == TransactionStatus.COMPLETED && lane.getStatus() != LaneStatus.AVAILABLE) {
            throw new InvalidInputException("Cannot complete transaction: lane is not available.");
        }

        bowlingBallServiceClient.getBowlingBallById(requestDTO.getBowlingBallId());
        shoeServiceClient.getShoeById(requestDTO.getShoeId());

        return lane;
    }
}