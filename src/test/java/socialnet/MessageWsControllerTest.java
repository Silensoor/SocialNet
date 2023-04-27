package socialnet;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
class MessageWsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendMessage() {
    }

    @Test
    void startTyping() {
    }

    @Test
    void stopTyping() {
    }

    @Test
    void editMessage() {
    }

    @Test
    void deleteMessages() {
    }

    @Test
    void recoverMessage() {
    }

    @Test
    void closeDialog() {
    }

}