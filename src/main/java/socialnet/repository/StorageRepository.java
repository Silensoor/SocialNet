package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Storage;
import socialnet.utils.Reflection;

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
}
