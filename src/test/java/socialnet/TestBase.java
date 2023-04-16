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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
//@Sql("/sql/schema.sql")
//@Sql("/sql/data.sql")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = TestBase.Initializer.class)
public abstract class TestBase {
    @ClassRule
    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.14");

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

    @BeforeAll
    public static void init() {
        container.start();
    }
}
