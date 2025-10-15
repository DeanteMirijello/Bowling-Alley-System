package com.bowling.apigateway.transaction.business;

import com.bowling.apigateway.transaction.presentation.TransactionRequestDTO;
import com.bowling.apigateway.transaction.presentation.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> getAllTransactions();
    TransactionResponseDTO getTransactionById(String transactionId);
    TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO);
    TransactionResponseDTO updateTransaction(String transactionId, TransactionRequestDTO requestDTO);
    void deleteTransaction(String transactionId);
}


