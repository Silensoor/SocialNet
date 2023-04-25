package socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.api.request.PostRq;
import socialnet.controller.PostsController;
import socialnet.exception.EntityNotFoundException;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { PostsControllerTest.Initializer.class })
@Sql(
    value = { "/sql/posts_controller_test.sql" },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
public class PostsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private final String TEST_EMAIL = "user1@email.com";

    @ClassRule
    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:12.14");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + container.getJdbcUrl(),
                "spring.datasource.username=" + container.getUsername(),
                "spring.datasource.password=" + container.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    public RequestPostProcessor authorization() {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(TEST_EMAIL));
            return request;
        };
    }

    @Test
    @DisplayName("Загрузка контекста")
    @Transactional
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    @DisplayName("Неавторизованный пользователь")
    @Transactional
    public void accessDenied() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1"))
            .andExpect(unauthenticated())
            .andDo(print());
    }

    @Test
    @DisplayName("Получение поста по существующему ID")
    @Transactional
    public void getPostByExistsId() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1").with(authorization()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data.id", is(1)))
            .andExpect(jsonPath("$.data.title", is("Post title #1")))
            .andDo(print());
    }

    @Test
    @DisplayName("Получение поста по несуществующему ID")
    @Transactional
    public void getPostByNotExistsId() throws Exception {
        /*mockMvc
            .perform(get("/api/v1/post/0").with(authorization()))
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("Post with id = 0 not found", result.getResolvedException().getMessage()))
            .andDo(print());*/
    }

    @Test
    @DisplayName("")
    @Transactional
    public void updatePostById() throws Exception {
        String expectedText = "Updated post";

        PostRq postRq = new PostRq();
        postRq.setTitle("Post title #1");
        postRq.setPost_text(expectedText);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String content = ow.writeValueAsString(postRq);

        mockMvc
            .perform(
                put("/api/v1/post/1")
                    .with(authorization())
                    .contentType("application/json")
                    .accept("application/json")
                    .content(content)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.post_text", is(expectedText)))
            .andDo(print());
    }

    @Test
    @DisplayName("")
    @Transactional
    public void deletePostById() throws Exception {
        mockMvc
            .perform(
                delete("/api/v1/post/1")
                .contentType("application/json")
                .accept("application/json")
            )
            .andExpect(status().isOk());
    }
}
