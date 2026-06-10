package com.ke.bella.openapi.config.initialize;

import com.ke.bella.openapi.config.web.OneTokenContextHolder;
import com.ke.bella.openapi.config.web.OneTokenServerContext;
import com.ke.bella.openapi.utils.NetworkUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.util.Optional;

@Slf4j
public class OneTokenContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	@Override
	public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
		Environment environment = configurableApplicationContext.getEnvironment();

		String enabled = environment.getProperty("one-token.server.initializer.enabled");
		if (!Boolean.parseBoolean(enabled)) {
			return;
		}

		try {
			String ip = NetworkUtils.getFirstNonLoopbackIp();
			Integer port = Optional.ofNullable(environment.getProperty("server.port")).map(Integer::valueOf).orElse(8080);
			String applicationName = environment.getProperty("spring.application.name");
			log.info("ip={},port={},applicationName={}", ip, port, applicationName);
			OneTokenServerContext context = OneTokenServerContext.builder()
				.ip(ip)
				.port(port)
				.applicationName(applicationName)
				.build();

			OneTokenContextHolder.setContext(context);
			log.info("BellaServerContextInitializer initialize() => ip={}, port={}, applicationName={}",
				ip, port, applicationName);
		} catch (Exception e) {
			log.warn("BellaServerContextInitializer initialize() failed, e: ", e);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10000;
	}
}
