package socialnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//@Component
public class Runner implements CommandLineRunner {
    @Autowired
    JdbcTemplate template;

    @Override
    public void run(String... args) throws Exception {
        template.execute("DELETE FROM post_comments");
        template.execute("DELETE FROM post2tag");
        template.execute("DELETE FROM posts");
        template.execute("DELETE FROM likes");
        template.execute("DELETE FROM persons");
        template.execute("DELETE FROM tags");
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/data.sql"));
        lines.forEach(x -> template.execute(x));
    }
}
