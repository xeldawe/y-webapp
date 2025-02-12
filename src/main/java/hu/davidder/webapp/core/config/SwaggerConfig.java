package hu.davidder.webapp.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order API")
                        .version("3.1.0") // Specify the OpenAPI version here
                        .description("Order management API"))
                .addSecurityItem(new SecurityRequirement().addList("x-api-key"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("x-api-key",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("x-api-key")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("orders")
                .pathsToMatch("/store/**")
                .build();
    }
}
