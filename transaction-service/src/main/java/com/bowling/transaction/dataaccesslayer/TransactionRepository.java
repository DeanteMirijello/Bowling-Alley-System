package com.bowling.transaction.dataaccesslayer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    @Query("{ 'transactionIdentifier.id': ?0 }")
    Transaction findByTransactionIdentifier_TransactionId(String transactionId);
}

