package com.eju.router.sdk;

import com.eju.router.sdk.exception.EjuException;


/*package*/ class ConsoleLogNode implements LogNode {

    @Override
    public void println(int priority, String tag, String content, Throwable throwable) {
        switch (priority) {
            case EjuLog.LOG_LEVEL_DEBUG:
                android.util.Log.d(tag, content);
                break;
            case EjuLog.LOG_LEVEL_ERROR:
                if (throwable == null) {
                    android.util.Log.e(tag, content);
                    return;
                }
                try {
                    if (throwable instanceof EjuException) {
                        android.util.Log.e(tag, throwable.getMessage());
                        return;
                    }
                    android.util.Log.e(tag, content, throwable);
                } catch (Exception ignored) {
                }
                break;
            case EjuLog.LOG_LEVEL_INFO:
                android.util.Log.i(tag, content);
                break;
            case EjuLog.LOG_LEVEL_VERBOSE:
                android.util.Log.v(tag, content);
                break;
            case EjuLog.LOG_LEVEL_WARNING:
                android.util.Log.w(tag, content);
                break;
            case EjuLog.LOG_LEVEL_NONE:
                break;
        }
    }
}
