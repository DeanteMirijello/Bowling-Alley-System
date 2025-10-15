package com.bowling.transaction.dataaccesslayer;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    private String id;

    private TransactionIdentifier transactionIdentifier;

    private String customerName;

    private String laneId;

    private String bowlingBallId;

    private String shoeId;

    private String laneZone;

    private BigDecimal totalPrice;

    private String dateCompleted;

    private TransactionStatus status;
}

