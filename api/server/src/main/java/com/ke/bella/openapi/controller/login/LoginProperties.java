package com.ke.bella.openapi.controller.login;

import lombok.Data;

@Data
public class LoginProperties {

	/**
	 * 登录的地址
	 */
	private String loginPageUrl;

	/**
	 * 认证Header
	 */
	private String authorizationHeader = "Authorization";
}
