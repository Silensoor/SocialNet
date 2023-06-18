package socialnet.controller;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import socialnet.api.request.UserRq;
import socialnet.config.KafkaConsumerConfig;
import socialnet.config.KafkaProducerConfig;
import socialnet.config.KafkaTopicConfig;
import socialnet.schedules.RemoveDeletedPosts;
import socialnet.schedules.RemoveOldCaptchasSchedule;
import socialnet.schedules.UpdateOnlineStatusScheduler;
import socialnet.security.jwt.JwtUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {UsersControllerTest.Initializer.class})
@Sql(scripts = "/sql/clear_tables.sql")
@Sql(scripts = "/sql/find-service-test.sql")
@SqlMergeMode(MERGE)
@MockBean(RemoveOldCaptchasSchedule.class)
@MockBean(RemoveDeletedPosts.class)
@MockBean(UpdateOnlineStatusScheduler.class)
@MockBean(KafkaController.class)
@MockBean(KafkaConsumerConfig.class)
@MockBean(KafkaProducerConfig.class)
@MockBean(KafkaTopicConfig.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private final String TEST_EMAIL = "user@email.com";

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
        assertThat(mockMvc).isNotNull();
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    @DisplayName("API GET /api/v1/users/me работает нормально.")
    @Sql(statements = "INSERT INTO persons (about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) VALUES ('S.T.A.R.S agent.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user@email.com', 'Chris', true, false, false, 'Redfield', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'OFFLINE', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Racoon', 'USA', 93, 633)")
    void getMyProfileTest() throws Exception {

        mockMvc.perform(get("/api/v1/users/me").with(getToken(TEST_EMAIL)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.about", is("S.T.A.R.S agent.")))
                .andExpect(jsonPath("$.data.city", is("Racoon")))
                .andExpect(jsonPath("$.data.country", is("USA")))
                .andExpect(jsonPath("$.data.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.data.online", is(true)))
                .andExpect(jsonPath("$.data.phone", is("966-998-0544")))
                .andExpect(jsonPath("$.data.photo", is("go86atavdxhcvcagbv")))
                .andExpect(jsonPath("$.data.birth_date", containsString("1972-11-14")))
                .andExpect(jsonPath("$.data.first_name", is("Chris")))
                .andExpect(jsonPath("$.data.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.is_blocked_by_current_user", is(false)))
                .andExpect(jsonPath("$.data.last_name", is("Redfield")))
                .andExpect(jsonPath("$.data.last_online_time", startsWith(LocalDate.now().toString())))
                .andExpect(jsonPath("$.data.messages_permission", is("adipiscing")))
                .andExpect(jsonPath("$.data.reg_date", containsString("2000-07-26")))
                .andExpect(jsonPath("$.data.user_deleted", is(false)))
                .andExpect(jsonPath("$.offset", is(0)))
                .andExpect(jsonPath("$.timestamp", lessThanOrEqualTo(System.currentTimeMillis())))
                .andExpect(jsonPath("$.total", is(0)))
                .andReturn();
    }

    @Test
    @DisplayName("API PUT /api/v1/users/me работает нормально.")
    @Sql(statements = "INSERT INTO persons (about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) VALUES ('S.T.A.R.S agent.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user@email.com', 'Chris', true, false, false, 'Redfield', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'OFFLINE', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Racoon', 'USA', 93, 633)")
    void updateUserInfoTest() throws Exception {
        UserRq userRq = new UserRq();
        userRq.setFirstName("first_name");
        userRq.setLastName("last_name");
        userRq.setCity("city");
        String body = new ObjectMapper().writeValueAsString(userRq);

        mockMvc.perform(put("/api/v1/users/me").with(getToken(TEST_EMAIL))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = '" + TEST_EMAIL + "'");
        resultSet.next();
        assertEquals("first_name", resultSet.getString("first_name"));
        assertEquals("last_name", resultSet.getString("last_name"));
        assertEquals("city", resultSet.getString("city"));
    }

    @Test
    @DisplayName("Удаление пользователя.")
    @Sql(statements = "INSERT INTO persons (about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) VALUES ('S.T.A.R.S agent.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user@email.com', 'Chris', true, false, false, 'Redfield', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'OFFLINE', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Racoon', 'USA', 93, 633)")
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me").with(getToken(TEST_EMAIL)))
                .andExpect(status().isOk())
                .andReturn();
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = '" + TEST_EMAIL + "'");
        resultSet.next();
        assertTrue(resultSet.getBoolean("is_deleted"));
    }

    @Test
    @DisplayName("Восстановление пользователя.")
    @Sql(statements = "INSERT INTO persons (about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) VALUES ('S.T.A.R.S agent.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user@email.com', 'Chris', true, false, true, 'Redfield', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'OFFLINE', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Racoon', 'USA', 93, 633)")
    void recoverUser() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/recover").with(getToken(TEST_EMAIL)))
                .andExpect(status().isOk())
                .andReturn();
        Connection connection = POSTGRES_CONTAINER.createConnection("");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM persons WHERE email = '" + TEST_EMAIL + "'");
        resultSet.next();
        assertFalse(resultSet.getBoolean("is_deleted"));
    }

    @Test
    @DisplayName("Find by City")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByCity() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru")).param("city", "Moscow-test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data[0].city", is("Moscow-test")));
    }

    @Test
    @DisplayName("Find by FirstName")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByFirstName() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru")).param("first_name", "Firstname2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data[0].first_name", is("Firstname2")));
    }

    @Test
    @DisplayName("Find by FirstName & Lastname")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByFirstAndLastName() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru"))
                        .param("first_name", "Firstname1")
                        .param("last_name", "Lastname1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data[0].first_name", is("Firstname1")))
                .andExpect(jsonPath("$.data[0].last_name", is("Lastname1")));
    }

    @Test
    @DisplayName("Find by Country")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByCountry() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru")).param("country", "Russia-test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[0].country", is("Russia-test")));
    }

    @Test
    @DisplayName("Find by Age Between")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByAgeFromTo() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru"))
                        .param("age_from", "1")
                        .param("age_to", "4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.length()", is(3)));
    }

    @Test
    @DisplayName("Find by Age To")
    @Sql(statements = "Insert into Persons (birth_date, email, first_name, last_name, is_approved, is_blocked, is_deleted, password, reg_date, city, country) values ('1982/06/02', 'mets@inbox.ru', 'Александр','Мец',false,false,false,'2a$10$D/tXegeuj2gciN/0N57.gepWAav83PxCASfv..7/OsdkFhZZXBXpm',now(), 'Moscow-test', 'Russia-test')")
    public void findByAgeTo() throws Exception{
        mockMvc.perform(get("/api/v1/users/search")
                        .with(getToken("mets@inbox.ru"))
                        .param("age_to", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }

    public RequestPostProcessor getToken(String email) {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(email));
            return request;
        };
    }

}