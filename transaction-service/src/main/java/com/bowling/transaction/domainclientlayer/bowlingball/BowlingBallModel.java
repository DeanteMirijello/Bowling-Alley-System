package com.bowling.transaction.domainclientlayer.bowlingball;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BowlingBallModel {

    private String id;

    private BallSize size;

    private String gripType;

    private String color;

    private BallStatus status;
}

