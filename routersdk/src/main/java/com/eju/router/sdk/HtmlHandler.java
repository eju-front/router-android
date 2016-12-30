package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;

/**
 * A HtmlHandler is a handler for HTML. This interface provides entrance to access HTML contents.
 *
 * @author tangqianwei
 */
public interface HtmlHandler {

    /**
     * handle the html contents
     *
     * @param url html url
     * @param contents html contents
     * @return handled contents
     * @throws EjuException if error
     */
    byte[] handle(String url, byte[] contents) throws EjuException;
    // need parameter in {@link #handle(String, byte[])} ?
}
