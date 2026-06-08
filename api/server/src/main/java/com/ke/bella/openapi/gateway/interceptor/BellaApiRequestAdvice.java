package com.ke.bella.openapi.gateway.interceptor;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.common.context.OneTokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@RestControllerAdvice(annotations = OneTokenAPI.class)
@Slf4j
public class BellaApiRequestAdvice extends RequestBodyAdviceAdapter {
    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        if(OneTokenContext.getOperatorIgnoreNull() == null) {
            if(body instanceof Operator) {
                if(profile.equals("dev") || profile.equals("ut")) {
                    OneTokenContext.setOperator(OneTokenContext.SYS);
                    return body;
                }
                Operator oper = (Operator) body;
                OneTokenContext.setOperator(oper);
            }
        }
        return body;
    }
}
