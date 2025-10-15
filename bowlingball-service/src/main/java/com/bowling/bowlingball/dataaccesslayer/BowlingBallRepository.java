package com.bowling.bowlingball.dataaccesslayer;

import com.bowling.bowlingball.dataaccesslayer.BowlingBall;
import com.bowling.bowlingball.dataaccesslayer.BowlingBallIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BowlingBallRepository extends JpaRepository<BowlingBall, BowlingBallIdentifier> {
}
