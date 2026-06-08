package com.ke.bella.openapi.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class OneTokenResponse<T> {
    private int code;
    private String message;
    private long timestamp;
    private T data;
    private String stacktrace;
}
