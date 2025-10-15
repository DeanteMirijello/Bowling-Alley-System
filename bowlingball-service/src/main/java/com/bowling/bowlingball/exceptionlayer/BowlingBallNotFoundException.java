package com.bowling.bowlingball.exceptionlayer;

public class BowlingBallNotFoundException extends RuntimeException {

    public BowlingBallNotFoundException(String id) {
        super("Bowling ball not found with ID: " + id);
    }
}
