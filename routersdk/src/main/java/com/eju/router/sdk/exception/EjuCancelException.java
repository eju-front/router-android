package com.eju.router.sdk.exception;

public class EjuCancelException extends EjuException {

    public EjuCancelException() {
        super("User canceled");
    }
}
