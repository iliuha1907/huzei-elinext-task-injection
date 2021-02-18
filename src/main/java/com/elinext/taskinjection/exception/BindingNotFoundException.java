package com.elinext.taskinjection.exception;

public class BindingNotFoundException extends RuntimeException {

    public BindingNotFoundException(String message) {
        super(message);
    }
}
