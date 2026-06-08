package com.ke.bella.openapi.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends OneTokenException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

	@Override
	public Integer getHttpCode() {
		return HttpStatus.NOT_FOUND.value();
	}

	@Override
	public String getType() {
		return "resource_not_found";
	}

}
