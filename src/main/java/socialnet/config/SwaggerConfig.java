package socialnet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class SwaggerConfig {
    private final Collection<ServerVariable> variables = new ArrayList<>();
    private final ArrayList<VendorExtension> extensions = new ArrayList<>();
    private final Server server1 = new Server("server1", "http://81.177.6.228:8086",
            "server1", variables, extensions);
    private final Server server2 = new Server("server2", "http://localhost:8086", "server2", variables, extensions);

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)


    @Bean
    public Docket docket() {
        Collection<ServerVariable> variables = new ArrayList<>();
        ArrayList<VendorExtension> extensions = new ArrayList<>();
        Server server1 = new Server("server1", "http://81.177.6.228:8086",
                "server1", variables, extensions);
        Server server2 = new Server("server2", "http://localhost:8086", "server2", variables, extensions);
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("Zerone API")
                        .description("API for social network")
                        .version("1.0")
                        .contact(new Contact("JAVA Pro 37 Group",
                                "http://81.177.6.228:8086",
                                "aaa@aaaa.aa"))
                        .build())
                .tags(new Tag("message-controller", "Endpoints for CRUD operations on messages WebSocket"))
                .servers(server1, server2)
                //.host("http://81.177.6.228:8086")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build();
        return docket;
    }
}
