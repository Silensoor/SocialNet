package socialnet.model;

import lombok.Data;

@Data
public class PostFile {
    private long id;
    private String name;
    private String path;
    private long postId;
}
