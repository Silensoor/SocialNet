package socialnet;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.service.KafkaService;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {BasicTest.Initializer.class})
@Testcontainers
@AutoConfigureMockMvc
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
@MockBean(KafkaService.class)
public class BasicTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:12.14");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
