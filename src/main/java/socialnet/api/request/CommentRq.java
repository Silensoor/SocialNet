package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRq {
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("parent_id")
    private Integer parentId;
}
