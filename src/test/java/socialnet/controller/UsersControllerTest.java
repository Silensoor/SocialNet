package socialnet.controller;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import socialnet.security.jwt.JwtUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("Postgres container is running")
    void test() {
        assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
    }

    @Test
    @DisplayName("API /api/v1/users/me работает нормально.")
    @Sql(statements = "INSERT INTO persons (about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) VALUES ('S.T.A.R.S agent.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user@email.com', 'Chris', true, false, false, 'Redfield', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'OFFLINE', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Racoon', 'USA', 93, 633)")
    void getMyProfileOnSuccessTest() throws Exception {

        mockMvc.perform(get("/api/v1/users/me").with(authorization()))
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

    public RequestPostProcessor authorization() {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(TEST_EMAIL));
            return request;
        };
    }

    public RequestPostProcessor getToken(String email) {
        return request -> {
            request.addHeader("authorization", jwtUtils.generateJwtToken(email));
            return request;
        };
    }

}