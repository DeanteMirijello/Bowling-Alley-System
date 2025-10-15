package com.bowling.transaction.businesslayer;

import com.bowling.transaction.presentationlayer.TransactionRequestDTO;
import com.bowling.transaction.presentationlayer.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> getAllTransactions();
    TransactionResponseDTO getTransactionById(String transactionId);
    TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO);
    TransactionResponseDTO updateTransaction(String transactionId, TransactionRequestDTO requestDTO);
    void deleteTransaction(String transactionId);
}
