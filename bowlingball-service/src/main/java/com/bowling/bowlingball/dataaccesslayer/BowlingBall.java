package com.bowling.bowlingball.dataaccesslayer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bowling_balls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BowlingBall {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private BowlingBallIdentifier id;

    @Enumerated(EnumType.STRING)
    private BallSize size;              //   SIX, EIGHT,TEN, TWELVE, FOURTEEN, SIXTEEN

    private String gripType;

    private String color;

    @Enumerated(EnumType.STRING)
    private BallStatus status;         //   AVAILABLE, IN_USE
}
