package socialnet;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-friendships-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FriendsTest extends AbstractTest{
    private final String TEST_EMAIL = "user1@email.com";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FriendsController friendsController;
    @Autowired
    private PersonService personService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private FriendsShipsRepository friendsShipsRepository;
    private String getToken(String email) {
        return jwtUtils.generateJwtToken(email);
    }
    @Test
    public void getFriendsTest() throws Exception{
        String token = getToken(TEST_EMAIL);
        getAuthenticated();

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
    public void getOutgoingRequests() throws Exception {
        String token = getToken(TEST_EMAIL);
        getAuthenticated();

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
    public void blocksUser() throws Exception {
        String token = getToken(TEST_EMAIL);
        getAuthenticated();

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
        getAuthenticated();

        this.mockMvc
                .perform(get("/api/v1/friends/recommendations").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email", is("fbrisset4@zimbio.com")))
                .andExpect(jsonPath("$.data[1].email", is("jjewell5@ebay.com")))
                .andReturn();
    }


    private void getAuthenticated() {
        LoginRq loginRq = new LoginRq();
        loginRq.setEmail(TEST_EMAIL);
        loginRq.setPassword("12345678");
        personService.getLogin(loginRq);
    }

    @Test
    @DisplayName("Загрузка контекста")
    public void contextLoads() {
        assertThat(friendsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
    }
}
