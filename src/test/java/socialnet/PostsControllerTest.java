package socialnet;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import socialnet.controller.PostsController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostsControllerTest extends AbstractTest {
    @Autowired
    private PostsController postsController;

    @Test
    @DisplayName("Загрузка контекста")
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(postsController).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
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
        authenticate("user1@email.com", "12345678");

        mockMvc
            .perform(get("/api/v1/post/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andReturn();
    }

    @Test
    @DisplayName("Получение поста по несуществующему ID")
    public void getPostByNotExistsId() throws Exception {
        authenticate("user1@email.com", "12345678");

        mockMvc
            .perform(get("/api/v1/post/0"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
