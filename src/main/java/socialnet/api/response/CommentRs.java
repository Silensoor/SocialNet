package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentRs {
    private PersonRs author;

    @JsonProperty("comment_text")
    private String commentText;

    private Long id;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    private Integer likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("sub_comments")
    private List<CommentRs> subComments;

    private String time;
}
