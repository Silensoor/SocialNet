package socialnet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CommentRs {
    private PersonRs author;

    @JsonProperty("comment_text")
    private String commentText;

    private Integer id;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    private Integer likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("post_id")
    private Integer postId;

    @JsonProperty("sub_comments")
    private List<CommentRs> subComments;

    private String time;
}
