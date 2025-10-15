package com.bowling.bowlingball.presentationlayer;

import com.bowling.bowlingball.dataaccesslayer.BallSize;
import com.bowling.bowlingball.dataaccesslayer.BallStatus;
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
