package com.ke.bella.openapi.common.exception;

import com.ke.bella.openapi.domain.protocol.ApiResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.ServletException;
import java.io.IOException;

public abstract class OneTokenException extends RuntimeException {

    protected OneTokenException(String message) {
        super(message);
    }

    protected OneTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static OneTokenException fromResponse(int httpCode, String message) {
        return new OneTokenException(message) {
            @Override
            public Integer getHttpCode() {
                return httpCode;
            }

            @Override
            public String getType() {
                return "Internal Exception";
            }
        };
    }

    public static OneTokenException fromException(Throwable e) {
        if(e instanceof OneTokenException) {
            return (OneTokenException) e;
        }
        if(e.getCause() instanceof OneTokenException) {
            return (OneTokenException) e.getCause();
        }
        return new OneTokenException(e.getMessage(), e) {
            @Override
            public Integer getHttpCode() {
                if(e instanceof IllegalArgumentException
                        || e instanceof UnsupportedOperationException
                        || e instanceof ServletException
                        || e instanceof MethodArgumentNotValidException
                        || e instanceof HttpMessageConversionException) {
                    return 400;
                }
                if(e instanceof IOException) {
                    return 502;
                }
                return 500;
            }

            @Override
            public String getType() {
                if(e instanceof IllegalArgumentException) {
                    return "Illegal Argument";
                }
                if(e instanceof UnsupportedOperationException) {
                    return "Unsupported Operation";
                }
                return "Internal Exception";
            }
        };
    }

    /**
     * 异常对应的http状态码
     *
     * @return
     */
    public abstract Integer getHttpCode();

    /**
     * 异常 type
     *
     * @return
     */
    public abstract String getType();

    public ApiResponse.OpenapiError convertToOpenapiError() {
        if(this instanceof ChannelException) {
            return ((ChannelException) this).getResponse();
        } else if(this instanceof SafetyCheckException) {
            return new ApiResponse.OpenapiError(this.getType(), this.getMessage(), this.getHttpCode(),
                    ((SafetyCheckException) this).getSensitive());
        } else {
            return new ApiResponse.OpenapiError(this.getType(), this.getMessage(), this.getHttpCode());
        }
    }

    public static class RateLimitException extends OneTokenException {
        public RateLimitException(String message) {
            super(message);
        }

        @Override
        public Integer getHttpCode() {
            return HttpStatus.TOO_MANY_REQUESTS.value();
        }

        @Override
        public String getType() {
            return HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase();
        }
    }

    public static class AuthorizationException extends OneTokenException {
        public AuthorizationException(String message) {
            super(message);
        }

        @Override
        public Integer getHttpCode() {
            return HttpStatus.UNAUTHORIZED.value();
        }

        @Override
        public String getType() {
            return HttpStatus.UNAUTHORIZED.getReasonPhrase();
        }
    }

    @Getter
    public static class SafetyCheckException extends OneTokenException {
        protected final Integer httpCode;
        protected final String type;
        protected final Object sensitive;

        public SafetyCheckException(Object sensitive) {
            super("safety_check_no_pass");
            this.httpCode = 400;
            this.type = "safety_check";
            this.sensitive = sensitive;
        }
    }

    @Getter
    public static class ChannelException extends OneTokenException {

        protected final Integer httpCode;
        protected final String type;
        private final ApiResponse.OpenapiError response;

        public ChannelException(Integer httpCode, String message) {
            this(httpCode, "Channel Exception", message);
        }

        public ChannelException(Integer httpCode, String type, String message) {
            this(httpCode, type, message, new ApiResponse.OpenapiError(type, message, httpCode));
        }

        public ChannelException(Integer httpCode, String type, String message, ApiResponse.OpenapiError error) {
            super(message);
            this.httpCode = httpCode >= 500 ? HttpStatus.SERVICE_UNAVAILABLE.value() : httpCode;
            if(httpCode >= 500) {
                message = "Provider returned: code: " + httpCode + " message: " + message;
            }
            this.type = type;
            if(error == null) {
                this.response = new ApiResponse.OpenapiError(type, message, httpCode);
            } else {
                error.setHttpCode(httpCode);
                this.response = error;
            }
        }
    }

    @Getter
    public static class ClientNotLoginException extends OneTokenException {

        private final String redirectUrl;

        public ClientNotLoginException(String redirectUrl) {
            super("Need to login");
            this.redirectUrl = redirectUrl;
        }

        @Override
        public Integer getHttpCode() {
            return 401;
        }

        @Override
        public String getType() {
            return "No Login";
        }
    }
}
