package com.bowling.shoe.exceptionlayer;

public class ShoeNotFoundException extends RuntimeException {

    public ShoeNotFoundException(String id) {
        super("Shoe not found with ID: " + id);
    }
}
