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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import socialnet.api.request.LoginRq;
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
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-friendships-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
    private final String TEST_PASSWORD = "12345678";

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

    private String getToken(String email) {
        return jwtUtils.generateJwtToken(email);
    }

    private void authenticate(String email, String password) {
        LoginRq loginRq = new LoginRq();
        loginRq.setEmail(email);
        loginRq.setPassword(password);

        personService.getLogin(loginRq);
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
        String token = getToken(TEST_EMAIL);
        authenticate(TEST_EMAIL, TEST_PASSWORD);

        this.mockMvc
            .perform(get("/api/v1/friends").header("authorization", token))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].email", is("user2@email.com")))
            .andExpect(jsonPath("$.data[1].email", is("kutting1@eventbrite.com")))
            .andReturn();
    }

    @Test
    @Transactional
    public void getOutgoingRequests() throws Exception {
        String token = getToken(TEST_EMAIL);
        authenticate(TEST_EMAIL, TEST_PASSWORD);

        this.mockMvc
            .perform(get("/api/v1/friends/outgoing_requests").header("authorization", token))
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
        String token = getToken(TEST_EMAIL);
        authenticate(TEST_EMAIL, TEST_PASSWORD);

        String startValue = friendsShipsRepository.findFriend(1L, 4L).get(0).getStatusName().toString();

        this.mockMvc
            .perform(post("/api/v1/friends/block_unblock/4").header("authorization", token))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String newValue = friendsShipsRepository.findFriend(1L, 4L).get(0).getStatusName().toString();
        assertThat(!startValue.equals(newValue)).isTrue();
    }

    public void getRecommendedFriends() throws Exception {
        String token = getToken(TEST_EMAIL);
        authenticate(TEST_EMAIL, TEST_PASSWORD);

        this.mockMvc
            .perform(get("/api/v1/friends/recommendations").header("authorization", token))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].email", is("fbrisset4@zimbio.com")))
            .andExpect(jsonPath("$.data[1].email", is("jjewell5@ebay.com")))
            .andReturn();
    }
}
