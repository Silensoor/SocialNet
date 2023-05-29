package socialnet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.util.ReflectionTestUtils;
import socialnet.BasicTest;
import socialnet.api.request.EmailRq;
import socialnet.api.request.LoginRq;
import socialnet.api.request.PasswordSetRq;
import socialnet.security.jwt.JwtUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static socialnet.repository.PersonRepository.PERSON_ROW_MAPPER;

@Sql(scripts = "/sql/clear_tables.sql")
@SqlMergeMode(MERGE)
public class PersonServiceTest extends BasicTest {
    static final String USER_EMAIL = "user@email.com";
    static final String USER_EMAIL_2 = "another@email.com";
    @Autowired
    PersonService personService;
    @MockBean
    JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        doReturn(USER_EMAIL).when(jwtUtils).getUserEmail(anyString());
    }

    @Test
    @DisplayName("Поднятие контекста.")
    public void contextLoads() {
        assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
        assertThat(jwtUtils).isNotNull();
        assertThat(personService).isNotNull();
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void delete() {
        var result = personService.delete("anything");
        assertNotNull(result);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?", PERSON_ROW_MAPPER, USER_EMAIL);
        assertTrue(person.getIsDeleted());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    @Sql(statements = "UPDATE persons SET is_deleted = true WHERE email = '" + USER_EMAIL + "'")
    void recover() {
        var result = personService.recover("anything");
        assertNotNull(result);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons WHERE email = ?", PERSON_ROW_MAPPER, USER_EMAIL);
        assertFalse(person.getIsDeleted());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getLogin() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "socialNet");
        doCallRealMethod().when(jwtUtils).generateJwtToken(anyString());
        var loginRq = new LoginRq();
        loginRq.setEmail(USER_EMAIL);
        loginRq.setPassword("12345678");
        var retVal = personService.getLogin(loginRq);
        assertNotNull(retVal.getData());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getMyProfile() {
        var retVal = personService.getMyProfile("anystring");
        assertNotNull(retVal.getData());
        var personRs = retVal.getData();
        assertTrue(personRs.getOnline());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void setNewEmail() {
        var emailRq = new EmailRq();
        emailRq.setEmail(USER_EMAIL_2);
        emailRq.setSecret("anything");
        var registerRs = personService.setNewEmail(emailRq);
        assertNotNull(registerRs);
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        assertEquals(USER_EMAIL_2, person.getEmail());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void resetPassword() {
        var person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        var oldPassword = person.getPassword();
        var passwordSetRq = new PasswordSetRq();
        passwordSetRq.setPassword("new_password");
        var retVal = personService.resetPassword("anything", passwordSetRq);
        assertNotNull(retVal);
        person = jdbcTemplate.queryForObject("SELECT * FROM persons", PERSON_ROW_MAPPER);
        var newPassword = person.getPassword();
        assertNotEquals(oldPassword, newPassword);
    }

    @Test
    void getLogout() {
        var retVal = personService.getLogout();
        assertNotNull(retVal);
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getUserById() {
        var retVal = personService.getUserById("anything", 1);
        assertNotNull(retVal);
    }

    @Test
    void getAuthPerson() {
        assertThrows(NullPointerException.class, () -> personService.getAuthPerson());
    }

    @Test
    void getAuthPersonId() {
        assertThrows(NullPointerException.class, () -> personService.getAuthPersonId());
    }

    @Test
    void checkLoginAndPassword() {

    }

    @Test
    void updateUserInfo() {
    }

    @Test
    void getPersonSettings() {
    }

    @Test
    void setSetting() {
    }
}