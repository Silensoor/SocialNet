package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRs {

    private PersonRs author;

    private List<CommentRs> comments;

    private Long id;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    private Integer likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("post_text")
    private String postText;

    private List<String> tags;

    private String time;

    private String title;

    private String type;
}
