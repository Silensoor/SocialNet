package socialnet;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import socialnet.controller.PostsController;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = JUnitTest.Initializer.class)
public class JUnitTest {
    @ClassRule
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.14");

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
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PostsController postsController;

    private String token = "";

    @BeforeEach
    public void setUp() {
        if (token.isEmpty()) {
            token = jwtUtils.generateJwtToken("user@email.com");
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    @DisplayName("Загрузка контекста")
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(postsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    @DisplayName("Неавторизованный пользователь")
    public void accessDenied() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1"))
            .andDo(print())
            .andExpect(unauthenticated())
            .andReturn();
    }

    @Test
    @DisplayName("Получение поста по существующему ID")
    public void getPostByExistsId() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1").header("Authorization", token))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @Test
    @DisplayName("Получение поста по несуществующему ID")
    public void getPostByNotExistsId() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/0").header("Authorization", token))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
