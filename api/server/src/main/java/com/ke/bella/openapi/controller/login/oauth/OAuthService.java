package com.ke.bella.openapi.controller.login.oauth;

import com.ke.bella.openapi.common.model.Operator;

import java.io.IOException;

public interface OAuthService {
	/**
	 * 获取OAuth认证URL
	 */
	String getAuthorizationUrl(String state);

	/**
	 * 处理OAuth回调
	 */
	Operator handleCallback(String code, String state) throws IOException;

	/**
	 * 获取提供商类型
	 */
	String getProviderType();
}
