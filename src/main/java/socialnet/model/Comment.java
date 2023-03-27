package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comment {
    private Integer id;

    private String commentText;

    private Boolean isBlocked;

    private Boolean isDeleted;

    private Timestamp time;

    private Integer parentId;

    private Integer authorId;

    private Integer postId;

}
