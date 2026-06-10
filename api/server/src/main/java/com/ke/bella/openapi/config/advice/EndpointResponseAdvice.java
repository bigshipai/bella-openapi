package com.ke.bella.openapi.config.advice;

import com.ke.bella.openapi.common.annotation.EndpointAPI;
import com.ke.bella.openapi.common.context.EndpointContext;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.domain.protocol.ApiResponse;
import com.ke.bella.openapi.domain.protocol.log.EndpointLogger;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.ke.bella.openapi.config.interceptor.ConcurrentStartInterceptor.ASYNC_REQUEST_MARKER;

@RestControllerAdvice(annotations = EndpointAPI.class)
@EndpointAPI
@Slf4j
public class EndpointResponseAdvice implements ResponseBodyAdvice<Object> {

	@Autowired
	private EndpointLogger logger;

	@Override
	public boolean supports(@NotNull MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType,
								  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
		ApiResponse apiResponse = body instanceof ApiResponse ? (ApiResponse) body : new ApiResponse();
		applyErrorStatus(apiResponse, response);
		if (isAsyncRequest(request)) {
			return body;
		}

		if (EndpointContext.getProcessData().getResponse() == null) {
			String requestId = EndpointContext.getProcessData().getRequestId();
			if (apiResponse.getError() != null) {
				logError(apiResponse.getError().getHttpCode(), requestId, apiResponse.getError().getMessage(), null);
			}
			EndpointContext.getProcessData().setResponse(apiResponse);
		}
		logger.log(EndpointContext.getProcessData());
		return body;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ApiResponse exceptionHandler(Exception exception) {
		String requestId = EndpointContext.getProcessData().getRequestId();
		OneTokenException e = OneTokenException.fromException(exception);
		logError(e.getHttpCode(), requestId, e.getMessage(), e);
		ApiResponse.OpenapiError error = e.convertToOpenapiError();
		ApiResponse apiResponse = ApiResponse.errorResponse(error);
		if (e instanceof OneTokenException.SafetyCheckException) {
			apiResponse.setSensitives(((OneTokenException.SafetyCheckException) e).getSensitive());
		}
		return apiResponse;
	}

	private void applyErrorStatus(ApiResponse apiResponse, ServerHttpResponse response) {
		if (apiResponse.getError() == null) {
			response.setStatusCode(HttpStatus.OK);
		} else {
			Integer httpCode = apiResponse.getError().getHttpCode();
			response.setStatusCode(httpCode == null ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.valueOf(httpCode));
		}
	}

	private boolean isAsyncRequest(ServerHttpRequest request) {
		if (request instanceof ServletServerHttpRequest) {
			return Boolean.TRUE.equals(((ServletServerHttpRequest) request).getServletRequest().getAttribute(ASYNC_REQUEST_MARKER));
		}
		return false;
	}

	private void logError(Integer httpCode, String requestId, String msg, Throwable e) {
		String str = "req_id :" + requestId + ",msg:" + msg;
		// 输出req_id方便根据req_id查询能力点日志
		if (httpCode == 500) {
			log.error(str, e);
		} else if (httpCode == 400 || httpCode == 401) {
			log.info(str, e);
		} else {
			log.warn(str, e);
		}
	}
}
