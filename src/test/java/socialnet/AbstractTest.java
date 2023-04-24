package socialnet;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.api.request.LoginRq;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.PersonService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = AbstractTest.Initializer.class)
public abstract class AbstractTest {
    @ClassRule
    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.14")
        .withReuse(true);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + container.getJdbcUrl(),
                "spring.datasource.username=" + container.getUsername(),
                "spring.datasource.password=" + container.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtils jwtUtils;

    @Autowired
    protected PersonService personService;

    @BeforeAll
    public static void init() {
        container.start();
    }

    protected String getToken(String email) {
        return jwtUtils.generateJwtToken(email);
    }

    protected void authenticate(String email, String password) {
        LoginRq loginRq = new LoginRq();
        loginRq.setEmail(email);
        loginRq.setPassword(password);

        personService.getLogin(loginRq);
    }
}
