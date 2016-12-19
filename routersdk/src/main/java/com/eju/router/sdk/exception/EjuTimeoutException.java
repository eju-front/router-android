package com.eju.router.sdk.exception;

/**
 * throws the Exception when request timeout.
 */
public class EjuTimeoutException extends EjuException {

    public EjuTimeoutException() {
        super("Timeout!");
    }
}
