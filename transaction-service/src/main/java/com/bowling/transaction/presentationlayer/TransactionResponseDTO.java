package com.bowling.transaction.presentationlayer;

import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import lombok.*;

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


