package com.bowling.apigateway.transaction.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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