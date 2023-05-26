package socialnet.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.api.request.EmailRq;
import socialnet.api.request.LoginRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.RegisterRs;
import socialnet.security.jwt.JwtUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {PersonServiceTest.Initializer.class})
@Sql(scripts = "/sql/clear_tables.sql")
@SqlMergeMode(MERGE)
class PersonServiceTest {
    static final String USER_EMAIL = "user@email.com";
    static final String USER_EMAIL_2 = "another@email.com";
    @Autowired
    PersonService personService;
    @MockBean
    JwtUtils jwtUtils;

    @Container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:12.14");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

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
    void delete() throws Exception {
        CommonRs result = personService.delete("anything");
        assertNotNull(result);
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = '" + USER_EMAIL + "'");
        resultSet.next();
        assertTrue(resultSet.getBoolean("is_deleted"));
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    @Sql(statements = "UPDATE persons SET is_deleted = true WHERE email = '" + USER_EMAIL + "'")
    void recover() throws Exception {
        CommonRs result = personService.recover("anything");
        assertNotNull(result);
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = '" + USER_EMAIL + "'");
        resultSet.next();
        assertFalse(resultSet.getBoolean("is_deleted"));
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getLogin() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "socialNet");
        doCallRealMethod().when(jwtUtils).generateJwtToken(anyString());
        LoginRq loginRq = new LoginRq();
        loginRq.setEmail(USER_EMAIL);
        loginRq.setPassword("12345678");
        CommonRs<PersonRs> commonRs = personService.getLogin(loginRq);
        assertNotNull(commonRs.getData());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void getMyProfile() {
        CommonRs<PersonRs> retVal = personService.getMyProfile("anystring");
        assertNotNull(retVal.getData());
        PersonRs personRs = retVal.getData();
        assertTrue(personRs.getOnline());
    }

    @Test
    @Sql(scripts = "/sql/create_random_user.sql")
    void setNewEmail() throws Exception {
        EmailRq emailRq = new EmailRq();
        emailRq.setEmail(USER_EMAIL_2);
        emailRq.setSecret("anything");
        RegisterRs registerRs = personService.setNewEmail(emailRq);
        assertNotNull(registerRs);
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons");
        resultSet.next();
        assertEquals(USER_EMAIL_2, resultSet.getString("email"));
    }

    @Test
    void resetPassword() {
    }

    @Test
    void getLogout() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void setCommonRs() {
    }

    @Test
    void setComplexRs() {
    }

    @Test
    void getAuthPerson() {
    }

    @Test
    void getAuthPersonId() {
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