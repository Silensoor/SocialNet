package socialnet.model.rs;

import lombok.AllArgsConstructor;
import lombok.Data;
import socialnet.model.db.Tag;
import socialnet.model.enums.PostType;

import java.util.List;

@Data
@AllArgsConstructor
public class PostRs {
    private PersonRs author;
    private List<CommentRs> comments;
    private long id;
    private boolean isBlocked;
    private int likes;
    private boolean myLike;
    private String postText;
    private List<Tag> tags;
    private String time;
    private String title;
    private PostType type;
}
