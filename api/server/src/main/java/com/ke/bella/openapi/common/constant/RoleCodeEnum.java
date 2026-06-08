package com.ke.bella.openapi.common.constant;

import lombok.Getter;

@Getter
public enum RoleCodeEnum {

	OWNER("owner", "Owner"),

	ADMIN("admin", "Admin"),

	MEMBER("member", "Member");

	RoleCodeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	private String code;

	private String desc;
}
