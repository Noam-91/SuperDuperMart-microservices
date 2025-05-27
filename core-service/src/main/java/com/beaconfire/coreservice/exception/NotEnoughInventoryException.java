package com.beaconfire.coreservice.exception;

public class NotEnoughInventoryException extends RuntimeException{
    public NotEnoughInventoryException(String message) {
        super(message);
    }
}
