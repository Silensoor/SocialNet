package socialnet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import socialnet.controller.PostsController;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostsTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PostsController postsController;

    @Autowired
    protected JwtUtils jwtUtils;

    private String getToken() {
        return jwtUtils.generateJwtToken("user@email.com");
    }

    @Test
    public void contextLoads() {
        assertThat(postsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    @WithAnonymousUser
    public void accessDeniedTest() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1"))
            .andDo(print())
            .andExpect(unauthenticated());
    }

    @Test
    public void getPostByIdTest() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/1").header("Authorization", getToken()))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void getPostByWrongIdTest() throws Exception {
        mockMvc
            .perform(get("/api/v1/post/0").header("Authorization", getToken()))
            .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
            .andReturn();
    }
}
