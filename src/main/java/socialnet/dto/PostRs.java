package socialnet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PostRs {

    private PersonRs author;

    private List<CommentRs> comments;

    private Integer id;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    private Long likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("post_text")
    private String postText;

    private List<String> tags;

    private String time;

    private String title;

    private String type;
}
