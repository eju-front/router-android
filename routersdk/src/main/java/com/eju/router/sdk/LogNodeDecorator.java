package com.eju.router.sdk;

/*package*/ class LogNodeDecorator implements LogNode {

    private LogNode logNode;
    private LogNode next;
    private boolean enabled = true;

    LogNodeDecorator(LogNode logNode) {
        this.logNode = logNode;
    }

    @Override
    public void println(int priority, String tag, String message, Throwable throwable) {
        if (enabled) {
            logNode.println(priority, tag, message, throwable);
        }
        if (next != null) {
            next.println(priority, tag, message, throwable);
        }
    }

    public void setNext(LogNode logNode) {
        this.next = logNode;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}