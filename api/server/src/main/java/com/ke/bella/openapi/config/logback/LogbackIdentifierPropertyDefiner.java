package com.ke.bella.openapi.config.logback;

import ch.qos.logback.core.PropertyDefinerBase;
import com.ke.bella.openapi.utils.NetworkUtils;

import java.util.UUID;

/**
 * Logback PropertyDefiner - 在 Logback 初始化时动态获取实例标识符
 * 使用方式：在 logback-spring.xml 中定义
 * <define name="INSTANCE_IDENTIFIER" class="com.ke.bella.openapi.server.InstanceIdentifierPropertyDefiner"/>
 */
public class LogbackIdentifierPropertyDefiner extends PropertyDefinerBase {

	private static volatile String cachedIdentifier;

	@Override
	public String getPropertyValue() {
		if (cachedIdentifier == null) {
			synchronized (LogbackIdentifierPropertyDefiner.class) {
				if (cachedIdentifier == null) {
					cachedIdentifier = generateIdentifier();
				}
			}
		}
		return cachedIdentifier;
	}

	private String generateIdentifier() {
		String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

		try {
			String ip = NetworkUtils.getFirstNonLoopbackIp();
			String identifier = String.format("%s-%s", ip, randomId);
			System.setProperty("one-token.instance.identifier", identifier);
			return identifier;
		} catch (Exception e) {
			System.err.println("Failed to get instance identifier: " + e.getMessage() + ", using random id: " + randomId);
			return randomId;
		}
	}
}
