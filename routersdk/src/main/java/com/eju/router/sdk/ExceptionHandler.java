package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;

/**
 * Created by Sidney on 2016/11/28.
 */

public interface ExceptionHandler {
    /**
     * handle {@link EjuException}
     * @param e the EjuException
     */
    void handle(EjuException e);
}
