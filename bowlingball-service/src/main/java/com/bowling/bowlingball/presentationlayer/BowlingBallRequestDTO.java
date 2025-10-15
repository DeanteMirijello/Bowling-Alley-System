package com.bowling.bowlingball.presentationlayer;

import com.bowling.bowlingball.dataaccesslayer.BallSize;
import com.bowling.bowlingball.dataaccesslayer.BallStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BowlingBallRequestDTO {

    @NotNull
    private BallSize size;

    @NotBlank
    private String gripType;

    @NotBlank
    private String color;

    @NotNull
    private BallStatus status;
}
