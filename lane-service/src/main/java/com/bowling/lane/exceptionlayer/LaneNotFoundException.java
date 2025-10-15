package com.bowling.lane.exceptionlayer;

public class LaneNotFoundException extends RuntimeException {

    public LaneNotFoundException(String id) {
        super("Lane not found with ID: " + id);
    }
}

