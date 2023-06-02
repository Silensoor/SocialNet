package socialnet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import socialnet.BasicTest;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.repository.FriendsShipsRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@Sql("/sql/friends-service-test.sql")
@Sql("/sql/create-friendships-before.sql")
@SqlMergeMode(MERGE)
public class FriendsServiceTest extends BasicTest{
        @Autowired
        private FriendsService friendsService;

        @Autowired
        private FriendsShipsRepository friendsShipsRepository;

        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private String getToken() {
            return jwtUtils.generateJwtToken("user1@email.com");
        }

        @Test
        @DisplayName("Context load")
        void contextLoads() {
            assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
            assertThat(friendsService).isNotNull();
            assertThat(friendsShipsRepository).isNotNull();
            assertThat(jwtUtils).isNotNull();
            assertThat(jdbcTemplate).isNotNull();
        }

        @Test
        @DisplayName("Get friends")
        void getFriendsTest() {
            CommonRs<List<PersonRs>> commonRs = friendsService.getFriends(getToken(), 0, 20);
            assertThat(commonRs.getData().size() == 2).isTrue();
            List<PersonRs> friends = commonRs.getData();
            assertThat(
                    friends.get(0).getEmail().equals("user2@email.com")
                            || friends.get(0).getEmail().equals("kutting1@eventbrite.com"))
                    .isTrue();
            assertThat(
                    friends.get(1).getEmail().equals("user2@email.com")
                            || friends.get(1).getEmail().equals("kutting1@eventbrite.com"))
                    .isTrue();
        }
        @Test
        @DisplayName("Block user")
        void userBlocksTest() {
            String firstActualFriendStatus = friendsService.getFriendStatus(1, 2);
            String secondActualFriendStatus = friendsService.getFriendStatus(1, 8);

            friendsService.userBlocks(getToken(), 2);
            friendsService.userBlocks(getToken(), 8);

            String firstFinalFriendStatus = friendsService.getFriendStatus(1, 2);
            String secondFinalFriendStatus = friendsService.getFriendStatus(1, 8);

            assertThat(firstActualFriendStatus.equals("FRIEND")
                            && secondActualFriendStatus.equals("BLOCKED"))
                    .isTrue();
            assertThat(firstFinalFriendStatus.equals("BLOCKED")
                            && secondFinalFriendStatus.equals("FRIEND"))
                    .isTrue();
        }
    }
