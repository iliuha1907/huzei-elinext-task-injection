package com.elinext.taskinjection.exception;

public class ConstructorNotFoundException extends RuntimeException {

    public ConstructorNotFoundException(String message) {
        super(message);
    }
}
