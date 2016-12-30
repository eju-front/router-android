package com.eju.router.sdk;

import java.util.regex.Pattern;

/**
 * Router url
 *
 * @author tangqianwei
 */
class RouterUrl {

    private String id;

    private String currentUrl;
    private Pattern urlPattern;

    private boolean needParameter = false;
    private boolean shouldBeIntercept = false;
    private HtmlHandler handler = null;

    RouterUrl(String urlRegex) {
        id = urlRegex;
        urlPattern = Pattern.compile(urlRegex);
    }

    public String getId() {
        return id;
    }

    boolean isMatch(String url) {
        return urlPattern.matcher(url).matches();
    }

    boolean isNeedParameter() {
        return needParameter;
    }

    void setNeedParameter(boolean needParameter) {
        this.needParameter = needParameter;
    }

    boolean shouldBeIntercept() {
        return shouldBeIntercept;
    }

    void setShouldBeIntercept(boolean shouldBeIntercept) {
        this.shouldBeIntercept = shouldBeIntercept;
    }

    HtmlHandler getHandler() {
        return handler;
    }

    void setHandler(HtmlHandler handler) {
        this.handler = handler;
    }

    String getCurrentUrl() {
        return currentUrl;
    }

    void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }
}
