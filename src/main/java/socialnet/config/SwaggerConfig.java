package socialnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("Zerone API")
                        .description("API for social network")
                        .version("1.0")
                        .build())
                .tags(new Tag("message-controller", "Endpoints for CRUD operations on messages WebSocket"))
                .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build();
    }
}
