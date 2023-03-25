package socialnet.model.db;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BlockHistory {
    private long id;
    private String action;
    private Timestamp time;
    private long comment_id;
    private long person_id;
    private long post_id;
}
