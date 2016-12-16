package com.eju.router.sdk.exception;

/**
 * throw when parameter goes wrong
 *
 * @author tangqianwei
 */

public class EjuParamException extends EjuException {

    public EjuParamException(String message) {
        super(message);
    }

    public EjuParamException(int code, String message) {
        super(code, message);
    }

    public EjuParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public EjuParamException(Throwable cause) {
        super(cause);
    }
}
