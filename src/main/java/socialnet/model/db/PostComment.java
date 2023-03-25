package socialnet.model.db;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PostComment {
    private long id;
    private String commentText;
    private boolean isBlocked;
    private boolean isDeleted;
    private Timestamp time;
    private long parentId;
    private long authorId;
    private long postId;
}
