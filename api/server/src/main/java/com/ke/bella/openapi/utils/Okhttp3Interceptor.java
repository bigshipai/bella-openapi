package com.ke.bella.openapi.utils;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.controller.apikey.dto.ApikeyInfo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@NoArgsConstructor
public class Okhttp3Interceptor implements Interceptor {

	private String host;

	// 在异步线程中使用时需要传入context
	private Map<String, Object> context;

	public Okhttp3Interceptor(String host, Map<String, Object> context) {
		this.host = stripProtocol(host);
		this.context = context;
	}

	private String stripProtocol(String url) {
		if (url == null) {
			return null;
		}
		if (url.startsWith("https://")) {
			return url.substring(8);
		}
		if (url.startsWith("http://")) {
			return url.substring(7);
		}
		return url;
	}

	@NotNull
	@SuppressWarnings("unchecked")
	@Override
	public Response intercept(@NotNull Chain chain) throws IOException {

		//这里主要解决内部互相调用的问题,不使用调用外部服务,仅仅是内部服务的信息透穿管理
		Request originalRequest = chain.request();
		if (!originalRequest.url().host().equals(host)) {
			return chain.proceed(originalRequest);
		}

		Map<String, Object> context = this.context;
		Map<String, String> headers = (Map<String, String>) Optional.ofNullable(context.get("headers")).orElse(new HashMap<>());
		Request.Builder otRequest = originalRequest.newBuilder();
		if (MapUtils.isNotEmpty(headers)) {
			headers.forEach(otRequest::header);
		}
		//这里主要也是透穿了AK的信息,以及用户的uid
		Operator op = (Operator) Optional.ofNullable(context.get("oper")).orElse(new Operator());
		String user = op.getUserId() == null ? op.getSourceId() : op.getUserId().toString();
		ApikeyInfo apikeyInfo = (ApikeyInfo) Optional.ofNullable(context.get("ak")).orElse(new ApikeyInfo());
		if (originalRequest.header("Authorization") == null && apikeyInfo.getApikey() != null) {
			otRequest.header("Authorization", "Bearer " + apikeyInfo.getApikey());
		}
		if (user == null) {
			user = apikeyInfo.getOwnerCode();
		}
		if (user != null) {
			otRequest.header("uc_id", user);
		}
		return chain.proceed(otRequest.build());
	}
}
