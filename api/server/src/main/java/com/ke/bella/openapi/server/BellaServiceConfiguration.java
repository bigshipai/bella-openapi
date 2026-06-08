package com.ke.bella.openapi.server;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ke.bella.openapi.client.OpenapiClient;
import com.ke.bella.openapi.request.BellaRequestFilter;
import com.ke.bella.openapi.server.intercept.AuthorizationInterceptor;
import com.ke.bella.openapi.server.intercept.ConcurrentStartInterceptor;
import com.ke.bella.openapi.utils.HttpUtils;

@Configuration
@EnableConfigurationProperties(OpenapiProperties.class)
public class BellaServiceConfiguration implements WebMvcConfigurer {

    private final ConcurrentStartInterceptor concurrentStartInterceptor;
    private final AuthorizationInterceptor authorizationInterceptor;
    private final OpenapiProperties openapiProperties;

    public BellaServiceConfiguration(
            @Lazy ConcurrentStartInterceptor concurrentStartInterceptor,
            @Lazy AuthorizationInterceptor authorizationInterceptor,
            @Lazy OpenapiProperties openapiProperties) {
        this.concurrentStartInterceptor = concurrentStartInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
        this.openapiProperties = openapiProperties;
    }

    @PostConstruct
    public void postConstruct() {
        HttpUtils.setOpenapiHost(openapiProperties.getHost());
    }

    @Bean
    public OpenapiClient openapiClient(OpenapiProperties properties) {
        return new OpenapiClient(properties.getHost(), properties.getServiceAk());
    }

    @Bean
    public OpenAiServiceFactory openAiServiceFactory(OpenapiProperties openapiProperties) {
        return new OpenAiServiceFactory(openapiProperties);
    }

    @Bean
    @ConditionalOnMissingBean(BellaRequestFilter.class)
    public FilterRegistrationBean<BellaRequestFilter> bellaRequestFilter(OpenapiProperties properties) {
        FilterRegistrationBean<BellaRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        BellaRequestFilter bellaRequestFilter = new BellaRequestFilter(properties.getService());
        filterRegistrationBean.setFilter(bellaRequestFilter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    @Bean
    public ConcurrentStartInterceptor concurrentStartInterceptor() {
        return new ConcurrentStartInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/console/**")
                .addPathPatterns("/v*/**")
                .order(100);
        registry.addInterceptor(concurrentStartInterceptor);
    }
}
