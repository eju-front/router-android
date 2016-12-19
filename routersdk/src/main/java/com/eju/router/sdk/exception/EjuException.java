package com.eju.router.sdk.exception;

public class EjuException extends Exception {

    public static final int UNKNOWN_ERROR = -1;
    public static final int RESOURCE_NOT_FOUND = 10000;
    public static final int ILLEGAL_PARAMETER = 11000;
    public static final int MISSING_PARAMETER = 12000;

    private int code;

    /**
     * init with message.
     * @param message the message of current {@link EjuException}
     */
    public EjuException(String message) {
        super(message);
    }

    /**
     * init with  code and message.
     * @param code the code such as {@link EjuException#UNKNOWN_ERROR} or {@link EjuException#ILLEGAL_PARAMETER}
     * @param message the message of current {@link EjuException}
     */
    public EjuException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * init with message and cause
     * @param message the message
     * @param cause the cause
     */
    public EjuException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * init with cause
     * @param cause the case
     */
    public EjuException(Throwable cause) {
        super(cause);
    }

    /**
     * get the code of {@link EjuException}
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * set code to the exception
     * @param code specified code
     */
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EjuException{");
        sb.append("code='").append(code).append('\'');
        sb.append(", message=").append(getMessage());
        sb.append('}');
        return sb.toString();
    }
}
