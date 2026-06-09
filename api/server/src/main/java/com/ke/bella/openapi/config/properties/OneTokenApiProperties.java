package com.ke.bella.openapi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("one-token.openapi")
public class OneTokenApiProperties {
	public String host;
	public String service;
	public String serviceAk;
}
