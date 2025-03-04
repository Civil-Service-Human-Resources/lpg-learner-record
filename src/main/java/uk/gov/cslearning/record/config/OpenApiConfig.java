package uk.gov.cslearning.record.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "info.app")
@RequiredArgsConstructor
public class OpenApiConfig {

    private final String name;
    private final String description;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(name)
                        .description(description)
                        .version("v1.0"));
    }

}
