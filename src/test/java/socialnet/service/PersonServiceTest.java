package socialnet.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;
import socialnet.AbstractTest;
import socialnet.api.response.CommonRs;
import socialnet.api.response.CurrencyRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.WeatherRs;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.UserDtoMapper;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest extends AbstractTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private PersonRepository personRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private CurrencyService currencyService;

    private UserDtoMapper userDtoMapper;

    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonService(
                jwtUtils,
                authenticationManager,
                personRepository,
                weatherService,
                currencyService,
                userDtoMapper);
    }

    @Test
    void getMyProfileOnSuccess() {
        WeatherRs weatherRs = new WeatherRs("city", "clouds", "date", "temp");
        CurrencyRs currencyRs = new CurrencyRs("eur", "usd");
        String token = "tokenJWT";
        doReturn("user1@email.com").when(jwtUtils).getUserEmail(anyString());
        doReturn(weatherRs).when(weatherService).getWeatherByCity(anyString());
        doReturn(currencyRs).when(currencyService).getCurrency(any(LocalDate.class));
        ReflectionTestUtils.setField(personService, "jwt", token);

        CommonRs<PersonRs> result = personService.getMyProfile("some_token");

        PersonRs actualPerson = result.getData();

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
        expectedPerson.setToken(token);
        expectedPerson.setUserDeleted(false);
        expectedPerson.setWeather(weatherRs);
        expectedPerson.setCurrency(currencyRs);

        assertEquals(expectedPerson, actualPerson);
    }
}