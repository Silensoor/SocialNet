package socialnet.model;

import lombok.Data;

@Data
public class Post2Tag {
    private Long id;

    private Long postId;

    private Long tagId;
}
