package socialnet.model.db;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Like {
    private long id;
    private String type;
    private long entityId;
    private Timestamp time;
    private long personId;
}
