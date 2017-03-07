package com.eju.router.sdk;

import android.app.Activity;

import java.util.regex.Pattern;

/**
 * Router url
 *
 * @author tangqianwei
 */
public abstract class RouterUrl {

    private String currentUrl;
    private Pattern urlPattern;

    void setUrlPattern(String url) {
        urlPattern = Pattern.compile(url);
    }

    boolean isMatch(String url) {
        return urlPattern.matcher(url).matches();
    }

    String getCurrentUrl() {
        return currentUrl;
    }

    void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public abstract boolean isNeedParameter();

    public abstract HtmlHandler getHandler();

    public abstract Class<? extends Activity> getTarget();
}
