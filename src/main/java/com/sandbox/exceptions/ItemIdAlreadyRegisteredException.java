package com.sandbox.exceptions;

public class ItemIdAlreadyRegisteredException extends RuntimeException {
    public ItemIdAlreadyRegisteredException() {
        super("Item ID already registered.");
    }
}
