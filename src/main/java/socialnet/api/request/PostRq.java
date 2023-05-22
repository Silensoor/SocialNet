package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRq {
    @NotBlank(message = "Post text can not be empty")
    @JsonProperty("post_text")
    private String postText;

    private List<String> tags = new ArrayList<>();

    @NotBlank(message = "Post title can not be empty")
    private String title;
}
