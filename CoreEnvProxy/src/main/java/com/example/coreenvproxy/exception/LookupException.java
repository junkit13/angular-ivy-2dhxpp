package com.example.coreenvproxy.exception;

public class LookupException extends RuntimeException {
    public LookupException(String message) {
        super(message);
    }

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
