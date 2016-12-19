package com.eju.router.sdk.exception;
/**
 * throws the Exception when request cancel.
 */
public class EjuCancelException extends EjuException {

    public EjuCancelException() {
        super("User canceled");
    }
}
