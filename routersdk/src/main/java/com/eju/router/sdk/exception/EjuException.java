package com.eju.router.sdk.exception;

public class EjuException extends Exception {

    public static final int UNKNOWN_ERROR = -1;
    public static final int RESOURCE_NOT_FOUND = 10000;
    public static final int ILLEGAL_PARAMETER = 11000;
    public static final int MISSING_PARAMETER = 12000;

    private int code;

    public EjuException(String message) {
        super(message);
    }

    public EjuException(int code, String message) {
        super(message);
        this.code = code;
    }

    public EjuException(String message, Throwable cause) {
        super(message, cause);
    }

    public EjuException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

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
