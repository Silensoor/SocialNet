package socialnet.model;

import lombok.Data;

@Data
public class Post2Tag {
    private long id;
    private long postId;
    private long tagId;
}
