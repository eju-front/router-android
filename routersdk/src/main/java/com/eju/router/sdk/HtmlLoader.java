package com.eju.router.sdk;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;

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
     * @param url url
     * @return contents in bytes or null when no contents could be loaded
     * @throws IOException if error in I/O
     */
    HttpClient.Response load(String url) throws IOException;
}
