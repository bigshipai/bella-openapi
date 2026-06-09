package com.ke.bella.openapi.controller.mock;

public interface MockSseWriter {
    void onOpen();

    void onWrite(Object chunk);

    void onCompletion();

    void onError(Throwable error);
}
