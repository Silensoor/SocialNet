package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BlockHistory {
    private Long id;
    private String action;
    private Timestamp time;
    private Long comment_id;
    private Long person_id;
    private Long post_id;
}
