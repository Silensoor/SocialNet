package socialnet.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import socialnet.api.response.CommonRs;
import socialnet.api.response.CurrencyRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.WeatherRs;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PersonServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private final String TEST_EMAIL = "user1@email.com";

    @Test
    void getMyProfileOnSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/users/me").with(authorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();

        PersonRs expectedPerson = new PersonRs();
        expectedPerson.setAbout("A rookie, who started his day with a wrong foot.");
        expectedPerson.setBirthDate(Timestamp.valueOf("1972-11-14 21:25:19"));
        expectedPerson.setCity("Bourg-en-Bresse");
        expectedPerson.setCountry("France");
        expectedPerson.setEmail("user1@email.com");
        expectedPerson.setFirstName("Leon");
        expectedPerson.setFriendStatus(null);
        expectedPerson.setId(1L);
        expectedPerson.setIsBlocked(false);
        expectedPerson.setIsBlockedByCurrentUser(false);
        expectedPerson.setLastName("Kennedy");
        expectedPerson.setLastOnlineTime(Timestamp.valueOf("2022-07-21 14:45:29"));
        expectedPerson.setMessagesPermission("adipiscing");
        expectedPerson.setOnline(true);
        expectedPerson.setPhone("966-998-0544");
        expectedPerson.setPhoto("go86atavdxhcvcagbv");
        expectedPerson.setRegDate(Timestamp.valueOf("2000-07-26 16:21:43"));
        expectedPerson.setToken(null);
        expectedPerson.setUserDeleted(false);
        expectedPerson.setWeather(null);
        expectedPerson.setCurrency(null);
    }

    public RequestPostProcessor authorization() {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(TEST_EMAIL));
            return request;
        };
    }
}