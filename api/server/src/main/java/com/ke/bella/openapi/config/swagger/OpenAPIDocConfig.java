package com.ke.bella.openapi.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("One-Token").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")));
    }

    @Bean
    public GroupedOpenApi metadataConsoleApi() {
        return GroupedOpenApi.builder()
                .group("Metadata Management")
                .pathsToMatch("/console/endpoint/**", "/console/model/**", "/console/channel/**", "/console/category/**")
                .addOpenApiCustomizer(customiseOpenApi())
                .build();
    }

    @Bean
    public GroupedOpenApi apiKeyConsoleApi() {
        return GroupedOpenApi.builder()
                .group("API Key Management")
                .pathsToMatch("/console/apikey/**")
                .addOpenApiCustomizer(customiseOpenApi())
                .build();
    }

    @Bean
    public GroupedOpenApi informationQueryApi() {
        return GroupedOpenApi.builder()
                .group("Information Query")
                .pathsToMatch("/v1/apikey/**", "/v1/meta/**")
                .addOpenApiCustomizer(customiseOpenApi())
                .build();
    }

    @Bean
    public GroupedOpenApi endpointApi() {
        return GroupedOpenApi.builder()
                .group("Endpoints")
                .pathsToMatch("/v1/**")
                .pathsToExclude("/v1/apikey/**", "/v1/meta/**")
                .addOpenApiCustomizer(customiseOpenApi())
                .build();
    }

	@Bean
	public GroupedOpenApi oauthApi() {
		return GroupedOpenApi.builder()
			.group("Oauth")
			.pathsToMatch("/openapi/**")
//			.pathsToExclude("/v1/apikey/**", "/v1/meta/**")
			.addOpenApiCustomizer(customiseOpenApi())
			.build();
	}



	private OpenApiCustomizer customiseOpenApi() {
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> operation.addParametersItem(new Parameter()
                        .in("header")
                        .name("Authorization")
                        .required(false)
                        .description("Authorization header")
                        .schema(new io.swagger.v3.oas.models.media.StringSchema())));
    }
}
