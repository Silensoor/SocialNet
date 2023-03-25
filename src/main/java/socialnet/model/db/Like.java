package socialnet.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Like {
    private long id;
    private String type;
    private long entityId;
    private Timestamp time;
    private long personId;
}
