package socialnet.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import socialnet.BasicTest;
import socialnet.model.Post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@Sql(scripts = "/sql/clear_tables.sql")
@Sql(scripts = "/sql/post_service.sql")
@SqlMergeMode(MERGE)
class PostRepositoryTest extends BasicTest {
    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("Поднятие контекста")
    void contextLoads() {
        assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
        assertThat(postRepository).isNotNull();
    }

    @Test
    @DisplayName("Поиск всех постов")
    void findAll() {
        assertEquals(4, postRepository.findAll().size());
    }

    @Test
    @DisplayName("Поиск всех постов по времени")
    void findAllByTime() {
        assertEquals(1, postRepository.findAll(0, 10, System.currentTimeMillis() - 3600000).size());
    }

    @Test
    @DisplayName("Получение всех неудалённых постов")
    void getAllCountNotDeleted() {
        assertEquals(3, postRepository.getAllCountNotDeleted());
    }

    @Test
    @DisplayName("Сохранение поста")
    void save() {
        Post post = new Post();
        post.setTitle("Title");
        post.setPostText("Text");
        post.setAuthorId(1L);
        post.setTime(null);
        post.setIsBlocked(false);
        post.setIsDeleted(false);

        postRepository.save(post);

        assertEquals(5, postRepository.findAll().size());
    }
}
