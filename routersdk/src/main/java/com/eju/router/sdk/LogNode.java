package com.eju.router.sdk;

/**
 * common log interface
 */
/*package*/ interface LogNode {

    /**
     * print log by current arguments
     *
     * @param priority priority
     * @param tag tag
     * @param message message
     * @param throwable throwable
     */
    void println(int priority, String tag, String message, Throwable throwable);
}