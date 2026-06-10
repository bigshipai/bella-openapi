package com.ke.bella.openapi.config;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.config.oauth.ProviderConditional;
import com.ke.bella.openapi.config.filter.OneTokenAuthFilter;
import com.ke.bella.openapi.controller.login.LoginProperties;
import com.ke.bella.openapi.controller.login.oauth.OAuthProperties;
import com.ke.bella.openapi.controller.login.oauth.providers.GithubOAuthService;
import com.ke.bella.openapi.controller.login.oauth.providers.GoogleOAuthService;
import com.ke.bella.openapi.controller.login.session.RedisSessionManager;
import com.ke.bella.openapi.controller.login.session.SessionManager;
import com.ke.bella.openapi.controller.login.session.SessionProperty;
import com.ke.bella.openapi.controller.login.user.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class OneTokenLoginConfiguration {

	public static final String redirectParameter = "redirect";

	private final RedisConnectionFactory redisConnectionFactory;

	public OneTokenLoginConfiguration(@Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
		this.redisConnectionFactory = redisConnectionFactory;
	}

	/**
	 * 跨域设置 — 确保跨域过滤器在最前面执行，避免其他过滤器阻断跨域预检请求。
	 */
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader("X-Redirect-Login");
		config.addExposedHeader("Location");
		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	private RedisTemplate<String, Operator> operatorRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Operator> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Operator.class));
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Operator.class));
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	@ConfigurationProperties(value = "one-token.session")
	public SessionProperty sessionProperty() {
		return new SessionProperty();
	}

	@Bean
	public SessionManager sessionManager(
		SessionProperty sessionProperty,
		@Autowired(required = false) IUserRepo userRepo) {
		if (redisConnectionFactory == null) {
			throw new IllegalStateException("missing redisConnectionFactory");
		}
		RedisSessionManager redisManager = new RedisSessionManager(
			sessionProperty,
			operatorRedisTemplate(redisConnectionFactory),
			new StringRedisTemplate(redisConnectionFactory));
		if (userRepo != null) {
			redisManager.setUserRepo(userRepo);
		}
		return redisManager;
	}

	@Bean
	@ConfigurationProperties(value = "one-token.oauth")
	public OAuthProperties oauthProperties() {
		return new OAuthProperties();
	}

	@Bean
	@ConfigurationProperties(value = "one-token.login")
	public LoginProperties loginProperties() {
		return new LoginProperties();
	}

	@Bean
	@ProviderConditional.ConditionalOnGoogleAuthEnable
	public GoogleOAuthService googleOAuthService(OAuthProperties properties) {
		return new GoogleOAuthService(properties);
	}

	@Bean
	@ProviderConditional.ConditionalOnGithubAuthEnable
	public GithubOAuthService githubOAuthService(OAuthProperties properties) {
		return new GithubOAuthService(properties);
	}

	@Bean
	public FilterRegistrationBean<OneTokenAuthFilter> sessionAuthFilter(LoginProperties properties, SessionManager sessionManager) {
		FilterRegistrationBean<OneTokenAuthFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new OneTokenAuthFilter(properties, sessionManager));
		//这里是值越大,越靠后执行
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 101);
		return registration;
	}
}
