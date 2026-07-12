package com.nursery.exception;

/** Thrown when a requested entity does not exist (or is soft-deleted). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
