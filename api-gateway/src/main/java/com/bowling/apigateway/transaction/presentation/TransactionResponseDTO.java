package com.bowling.apigateway.transaction.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {

    private String transactionId;

    private String customerName;

    private String laneId;

    private String bowlingBallId;

    private String shoeId;

    private String laneZone;

    private BigDecimal totalPrice;

    private String dateCompleted;

    private TransactionStatus status;
}
