package socialnet;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import socialnet.controller.FriendsController;
import socialnet.repository.FriendsShipsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-friendships-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FriendsTest extends AbstractTest{
    private final String TEST_EMAIL = "user1@email.com";
    private final String TEST_PASSWORD = "12345678";

    @Autowired
    private FriendsController friendsController;

    @Autowired
    private FriendsShipsRepository friendsShipsRepository;

    @Test
    @DisplayName("Загрузка контекста")
    public void contextLoads() {
        assertThat(friendsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
        assertThat(mockMvc).isNotNull();
        assertThat(friendsShipsRepository).isNotNull();
    }

    @Test
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
