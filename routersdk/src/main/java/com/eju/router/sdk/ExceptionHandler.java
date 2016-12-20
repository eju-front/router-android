package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;

/**
 * @author Sidney
 */
public interface ExceptionHandler {

    /**
     * handle {@link EjuException}
     * @param e the EjuException
     */
    void handle(EjuException e);
}
