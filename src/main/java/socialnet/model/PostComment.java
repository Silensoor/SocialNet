package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
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
