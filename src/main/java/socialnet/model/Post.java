package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Post implements Comparable<Post>{
    private long id;
    private boolean isBlocked;
    private boolean isDeleted;
    private String postText;
    private Timestamp time;
    private Timestamp timeDelete;
    private String title;
    private long authorId;

    @Override
    public int compareTo(Post o) {
        return o.getTime().compareTo(this.time);
    }
}
