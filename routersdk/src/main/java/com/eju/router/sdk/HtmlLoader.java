package com.eju.router.sdk;

import java.io.IOException;


/**
 * if html page will be intercepted by {@link AbstractInterceptor}, this class will
 * load the page.
 *
 * @author tangqianwei
 */
interface HtmlLoader {

    /**
     * load the html page
     *
     * @param request request
     * @return contents in bytes or null when no contents could be loaded
     * @throws IOException if error in I/O
     */
    HttpClient.Response load(HttpClient.Request request) throws IOException;
}
