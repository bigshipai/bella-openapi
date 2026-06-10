package com.ke.bella.openapi.config.advice;

import com.ke.bella.openapi.common.annotation.OneTokenAPI;
import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.common.response.OneTokenResponse;
import com.ke.bella.openapi.utils.JacksonUtils;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 主要做了一些响应的规范化,以及异常处理的点
 */
@RestControllerAdvice(annotations = OneTokenAPI.class)
@Slf4j
public class OneTokenAPIResponseAdvice implements ResponseBodyAdvice<Object> {
	private static String stacktrace(Throwable e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType,
								  @NotNull MediaType selectedContentType,
								  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  @NotNull ServerHttpRequest request, ServerHttpResponse response) {
		response.getHeaders().add("Cache-Control", "no-cache");
		response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
		if (body instanceof OneTokenResponse) {
			response.setStatusCode(HttpStatus.valueOf(((OneTokenResponse) body).getCode()));
			return body;
		}

		OneTokenResponse<Object> resp = new OneTokenResponse<>();
		resp.setCode(200);
		resp.setTimestamp(System.currentTimeMillis());
		resp.setData(body);

		if (body instanceof String) {
			return JacksonUtils.serialize(resp);
		}
		return resp;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public OneTokenResponse<?> exceptionHandler(Exception e) {
		int code = 500;
		String msg = e.getLocalizedMessage();
		if (e instanceof IllegalArgumentException
			|| e instanceof ServletException
			|| e instanceof MethodArgumentNotValidException
			|| e instanceof BizParamCheckException) {
			code = 400;
		}
		if (e instanceof OneTokenException) {
			code = ((OneTokenException) e).getHttpCode();
		}

		if (code == 500) {
			log.warn(e.getMessage(), e);
		} else {
			log.info(e.getMessage(), e);
		}

		OneTokenResponse<?> er = new OneTokenResponse<>();
		er.setCode(code);
		er.setTimestamp(System.currentTimeMillis());

		// 一些特殊异常类型返回给调用方的错误信息提示需要按照指定的规则给值
		if (e instanceof MethodArgumentNotValidException) {
			er.setMessage(((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage());
		} else {
			er.setMessage(msg);
		}

		if (code == 500) {
			er.setStacktrace(stacktrace(e));
		}

		return er;
	}
}
