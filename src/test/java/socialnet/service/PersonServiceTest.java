package socialnet.service;

import org.jetbrains.annotations.NotNull;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.api.response.CommonRs;
import socialnet.controller.UsersControllerTest;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {PersonServiceTest.Initializer.class})
@Sql(scripts = "/sql/clear_tables.sql")
@SqlMergeMode(MERGE)
class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @MockBean
    private JwtUtils jwtUtils;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:12.14");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
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
        doReturn("user@email.com").when(jwtUtils).getUserEmail(anyString());
        CommonRs result = personService.delete("anything");
        assertNotNull(result);
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = 'user@email.com'");
        resultSet.next();
        assertTrue(resultSet.getBoolean("is_deleted"));
    }

    @Test
    void recover() {
    }

    @Test
    void getLogin() {
    }

    @Test
    void getMyProfile() {
    }

    @Test
    void setNewEmail() {
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