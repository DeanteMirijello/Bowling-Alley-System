package com.bowling.transaction.presentationlayer;

import com.bowling.transaction.dataaccesslayer.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotBlank
    private String customerName;

    @NotBlank
    private String laneId;

    @NotBlank
    private String bowlingBallId;

    @NotBlank
    private String shoeId;

    @NotNull
    private TransactionStatus status;
}


