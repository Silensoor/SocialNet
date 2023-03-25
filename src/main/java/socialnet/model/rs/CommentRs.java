package socialnet.model.rs;

import lombok.Data;

import java.util.List;

@Data
public class CommentRs {
    private PersonRs author;
    private int id;
    private boolean isBlocked;
    private boolean isDeleted;
    private int likes;
    private boolean myLike;
    private int parentId;
    private int postId;
    private List<CommentRs> subComments;
    private String time;
}
