package com.lc.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.ImplicitGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author Liu Jianwei<liujianwei@credit.com>
 * @date 2016-3-30
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("")
                .apiInfo(apiInfo())
                .select()
                .paths(paths())
                .build()
                .securitySchemes(newArrayList(apiKey()))
                .securityContexts(newArrayList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("【O2O】[ app-microservice ][test-service]")
                .description("test服务")
                .termsOfServiceUrl("")
                .contact("test1")
                .license("")
                .licenseUrl("")
                .version("2.0")
                .build();
    }

    private Predicate<String> paths() {
        return or(
                //配置入口，多个的话就用多个regex配置，以逗号隔开
                regex("/boot.*")
        );
    }

    @Bean
    public Docket userApi() {
        AuthorizationScope[] authScopes = new AuthorizationScope[1];
        authScopes[0] = new AuthorizationScopeBuilder()
                .scope("read")
                .description("read access")
                .build();
        SecurityReference securityReference = SecurityReference.builder()
                .reference("test")
                .scopes(authScopes)
                .build();

        ArrayList<SecurityContext> securityContexts = newArrayList(SecurityContext.builder().securityReferences
                (newArrayList(securityReference)).build());
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(newArrayList(new BasicAuth("test")))
                .securityContexts(securityContexts)
                .groupName("user-api")
                .apiInfo(apiInfo())
                .select()
                .paths(paths())
                .build();
    }

    @Bean
    SecurityContext securityContext() {
        AuthorizationScope readScope = new AuthorizationScope("read:pets", "read your pets");
        AuthorizationScope[] scopes = new AuthorizationScope[1];
        scopes[0] = readScope;
        SecurityReference securityReference = SecurityReference.builder()
                .reference("petstore_auth")
                .scopes(scopes)
                .build();

        return SecurityContext.builder()
                .securityReferences(newArrayList(securityReference))
                .forPaths(paths())
                .build();
    }

    @Bean
    SecurityScheme oauth() {
        return new OAuthBuilder()
                .name("petstore_auth")
                .grantTypes(grantTypes())
                .scopes(scopes())
                .build();
    }

    @Bean
    SecurityScheme apiKey() {
        return new ApiKey("api_key", "api_key", "header");
    }

    List<AuthorizationScope> scopes() {
        return newArrayList(
                new AuthorizationScope("write:pets", "modify pets in your account"),
                new AuthorizationScope("read:pets", "read your pets"));
    }

    List<GrantType> grantTypes() {
        GrantType grantType = new ImplicitGrantBuilder()
                .loginEndpoint(new LoginEndpoint("http://petstore.swagger.io/api/oauth/dialog"))
                .build();
        return newArrayList(grantType);
    }

    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration("abc", "123", "pets", "petstore", "123", ApiKeyVehicle.HEADER, "", ",");
    }
}
