package socialnet;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "Zerone API",
                version = "1.0",
                description = "API for social network",
                contact = @Contact(
                        name = "37 group",
                        email = "runcodenow@gmail.com"
                ),
                license = @License(
                        name = "licence",
                        url = "http://licence"
                )
        )
)
public class SocialNetApp {
    public static void main(String[] args) {
        SpringApplication.run(SocialNetApp.class, args);
    }
}