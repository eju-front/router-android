package com.eju.router.sdk;

import java.io.IOException;


/**
 * if html page will be intercepted by {@link Interceptor}, this class will
 * load the page.
 *
 * @author tangqianwei
 */
interface HtmlLoader {

    /**
     * load the html page
     *
     * @param url url
     * @return contents in bytes
     * @throws IOException if error in I/O
     */
    byte[] load(String url) throws IOException;
}
