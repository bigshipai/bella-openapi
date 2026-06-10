package com.ke.bella.openapi.common.simulation;

import com.ke.bella.openapi.domain.protocol.completion.StreamCompletionResponse;

public interface FunctionCallListener {

    default void onMessage(StreamCompletionResponse msg) {
    }

    default void onFinish() {
    }

    default void onError(String msg) {
    }

}
