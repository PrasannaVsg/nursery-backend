package com.nursery.exception;

/** Thrown when a server-enforced business rule is violated (over-capacity, dispatch &gt; remaining, ...). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
