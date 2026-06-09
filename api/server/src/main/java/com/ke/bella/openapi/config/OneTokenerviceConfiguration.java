package com.ke.bella.openapi.config;

import com.ke.bella.openapi.client.OneTokenServerClient;
import com.ke.bella.openapi.common.constant.EntityConstants;
import com.ke.bella.openapi.gateway.interceptor.AuthorizationInterceptor;
import com.ke.bella.openapi.gateway.interceptor.ConcurrentStartInterceptor;
import com.ke.bella.openapi.gateway.interceptor.MonthQuotaInterceptor;
import com.ke.bella.openapi.gateway.interceptor.QpsRateLimitInterceptor;
import com.ke.bella.openapi.utils.HttpUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(OneTokenApiProperties.class)
public class OneTokenerviceConfiguration implements WebMvcConfigurer {

	public static final List<String> endpointPathPatterns = Arrays.stream(EntityConstants.SystemBasicEndpoint.values())
		.map(EntityConstants.SystemBasicEndpoint::getEndpoint).collect(Collectors.toList());

	private final ConcurrentStartInterceptor concurrentStartInterceptor;
	private final AuthorizationInterceptor authorizationInterceptor;
	private final MonthQuotaInterceptor monthQuotaInterceptor;
	private final QpsRateLimitInterceptor qpsRateLimitInterceptor;
	private final OneTokenApiProperties oneTokenApiProperties;

	public OneTokenerviceConfiguration(
		ConcurrentStartInterceptor concurrentStartInterceptor,
		AuthorizationInterceptor authorizationInterceptor,
		MonthQuotaInterceptor monthQuotaInterceptor,
		QpsRateLimitInterceptor qpsRateLimitInterceptor,
		OneTokenApiProperties oneTokenApiProperties) {
		this.concurrentStartInterceptor = concurrentStartInterceptor;
		this.authorizationInterceptor = authorizationInterceptor;
		this.monthQuotaInterceptor = monthQuotaInterceptor;
		this.qpsRateLimitInterceptor = qpsRateLimitInterceptor;
		this.oneTokenApiProperties = oneTokenApiProperties;
	}

	@PostConstruct
	public void postConstruct() {
		HttpUtils.setOpenapiHost(oneTokenApiProperties.getHost());
	}

	@Bean
	public OneTokenServerClient openapiClient(OneTokenApiProperties properties) {
		return new OneTokenServerClient(properties.getHost(), properties.getServiceAk());
	}

	@Bean
	public OpenAiServiceFactory openAiServiceFactory(OneTokenApiProperties oneTokenApiProperties) {
		return new OpenAiServiceFactory(oneTokenApiProperties);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 认证拦截器（order=100）- 对 /console/** 和 /v*/** 路径做 API Key 认证 + 权限校验
		registry.addInterceptor(authorizationInterceptor)
			.addPathPatterns("/console/**")
			.addPathPatterns("/v*/**")
			.order(100);

		// QPS 限流拦截器（order=109）- 在 AuthorizationInterceptor(100) 之后，MonthQuotaInterceptor(110) 之前
		registry.addInterceptor(qpsRateLimitInterceptor)
			.addPathPatterns(endpointPathPatterns)
			.order(109);

		// 月额度拦截器（order=110）
		registry.addInterceptor(monthQuotaInterceptor)
			.addPathPatterns(endpointPathPatterns)
			.order(110);

		// 异步请求标记拦截器（全局）- 标记异步 dispatch 防止拦截器重复执行
		registry.addInterceptor(concurrentStartInterceptor);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer
			.defaultContentType(MediaType.APPLICATION_JSON)
			.ignoreAcceptHeader(true);
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(256 * 1024);
		container.setMaxBinaryMessageBufferSize(256 * 1024);
		return container;
	}
}
