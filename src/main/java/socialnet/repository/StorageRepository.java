package socialnet.repository;

import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Person;
import socialnet.model.Storage;
import socialnet.utils.Reflection;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StorageRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

    public void insertStorage(Storage storage) {
        String sql = "Insert into Storage " + reflection.getFieldNames(storage, "id");
        Object[] values = reflection.getValues(storage, "id");
        jdbcTemplate.update(sql, values);
    }

    public Optional<String> getPhotoUrl(Long personId) {
        return jdbcTemplate.query("Select filename from Storage Where owner_id = ?",
                new BeanPropertyRowMapper<>(String.class),
                personId).stream().findAny();
    }
}
