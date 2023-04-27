package socialnet;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.controller.FriendsController;
import socialnet.repository.FriendsShipsRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.PersonService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { FriendsTest.Initializer.class })
@Sql(value = {"/sql/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-friendships-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FriendsTest {
    @Autowired
    private FriendsController friendsController;

    @Autowired
    private FriendsShipsRepository friendsShipsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PersonService personService;

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
        assertThat(friendsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(friendsShipsRepository).isNotNull();
    }

    @Test
    @Transactional
    public void getFriendsTest() throws Exception{
        this.mockMvc
                .perform(get("/api/v1/friends").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].email", is("kutting1@eventbrite.com")))
                .andExpect(jsonPath("$.data[1].email", is("user2@email.com")))
                .andReturn();
    }

    @Test
    @Transactional
    public void getOutgoingRequests() throws Exception {
        this.mockMvc
            .perform(get("/api/v1/friends/outgoing_requests").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data[0].email", is("nwickey2@ibm.com")))
            .andExpect(jsonPath("$.data[1].email", is("dsuermeiers3@gmpg.org")))
            .andReturn();
    }
    @Test
    @Transactional
    public void blocksUser() throws Exception {

        String startValue = friendsShipsRepository.findFriend(1L, 4L).getStatusName().toString();


        this.mockMvc
            .perform(post("/api/v1/friends/block_unblock/4").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String newValue = friendsShipsRepository.findFriend(1L, 4L).getStatusName().toString();
        assertThat(!startValue.equals(newValue)).isTrue();
    }

    public void getRecommendedFriends() throws Exception {
        this.mockMvc
            .perform(get("/api/v1/friends/recommendations").with(authorization()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].email", is("fbrisset4@zimbio.com")))
            .andExpect(jsonPath("$.data[1].email", is("jjewell5@ebay.com")))
            .andReturn();
    }
}
