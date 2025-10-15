package com.bowling.apigateway.bowlingball.presentation;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BowlingBallRequestDTO {
    private BallSize size;
    private String gripType;
    private String color;
    private BallStatus status;
}

