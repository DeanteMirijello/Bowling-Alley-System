package com.bowling.apigateway.bowlingball.presentation;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BowlingBallResponseDTO {
    private String id;
    private BallSize size;
    private String gripType;
    private String color;
    private BallStatus status;
}

