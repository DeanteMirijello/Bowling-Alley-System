package com.bowling.apigateway.transaction.business;

import com.bowling.apigateway.transaction.domainclient.TransactionClient;
import com.bowling.apigateway.transaction.presentation.TransactionRequestDTO;
import com.bowling.apigateway.transaction.presentation.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionClient transactionClient;

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionClient.getAllTransactions();
    }

    @Override
    public TransactionResponseDTO getTransactionById(String transactionId) {
        return transactionClient.getTransactionById(transactionId);
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) {
        return transactionClient.createTransaction(requestDTO);
    }

    @Override
    public TransactionResponseDTO updateTransaction(String transactionId, TransactionRequestDTO requestDTO) {
        return transactionClient.updateTransaction(transactionId, requestDTO);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        transactionClient.deleteTransaction(transactionId);
    }
}

