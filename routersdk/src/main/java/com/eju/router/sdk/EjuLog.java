package com.eju.router.sdk;

/*package*/ class EjuLog {

    private static final String LOG_TAG = "EjuLog";

    /**
     * Log level: verbose
     */
    static final int LOG_LEVEL_VERBOSE = 2;
    /**
     * Log level: debug
     */
    static final int LOG_LEVEL_DEBUG = 3;
    /**
     * Log level: info
     */
    static final int LOG_LEVEL_INFO = 4;
    /**
     * Log level: warning
     */
    static final int LOG_LEVEL_WARNING = 5;
    /**
     * Log level: error
     */
    static final int LOG_LEVEL_ERROR = 6;
    /**
     * Log level: none
     */
    static final int LOG_LEVEL_NONE = Integer.MAX_VALUE;

    private static LogNodeDecorator decorator = new LogNodeDecorator(new ConsoleLogNode());

    private static int logLevel = LOG_LEVEL_VERBOSE;

    public static void setDecorator(LogNodeDecorator decorator) {
        EjuLog.decorator = decorator;
    }

    private static boolean shouldShow(final int logLevel) {
        return logLevel >= EjuLog.logLevel;
    }

    public static void setLogLevel(int logLevel) {
        EjuLog.logLevel = logLevel;
    }

    public static void d(final String message) {
        if (shouldShow(LOG_LEVEL_DEBUG)) {
            d(LOG_TAG, message);
        }
    }

    public static void d(final String tag, final String message) {
        if (shouldShow(LOG_LEVEL_DEBUG)) {
            decorator.println(LOG_LEVEL_DEBUG, tag, message, null);
        }
    }

    public static void e(final String message) {
        if (shouldShow(LOG_LEVEL_ERROR)) {
            e(LOG_TAG, message);
        }
    }

    public static void e(final String tag, final String message) {
        if (shouldShow(LOG_LEVEL_ERROR)) {
            decorator.println(LOG_LEVEL_ERROR, tag, message, null);
        }
    }

    public static void e(final String message, final Throwable throwable) {
        e(LOG_TAG, message, throwable);
    }

    public static void e(final String tag, final String message,
                         final Throwable throwable) {
        if (shouldShow(LOG_LEVEL_ERROR)) {
            decorator.println(LOG_LEVEL_ERROR, tag, message, throwable);
        }
    }

    public static boolean isLoggable() {
        return logLevel != LOG_LEVEL_NONE;
    }
}
