package com.ke.bella.openapi.config;

import java.util.concurrent.atomic.AtomicReference;

public class OneTokenContextHolder {

    private static final AtomicReference<OneTokenServerContext> CONTEXT = new AtomicReference<>();

    public static void setContext(OneTokenServerContext context) {
        CONTEXT.set(context);
    }

    public static OneTokenServerContext getContext() {
        return CONTEXT.get();
    }

    public static String getIp() {
        OneTokenServerContext context = getContext();
        return context != null ? context.getIp() : null;
    }

    public static Integer getPort() {
        OneTokenServerContext context = getContext();
        return context != null ? context.getPort() : null;
    }

    public static String getApplicationName() {
        OneTokenServerContext context = getContext();
        return context != null ? context.getApplicationName() : null;
    }
}
