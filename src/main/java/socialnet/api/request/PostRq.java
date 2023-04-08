package socialnet.api.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostRq {
    @NotBlank(message = "Post text can not be empty")
    private String post_text;

    private List<String> tags = new ArrayList<>();

    @NotBlank(message = "Post title can not be empty")
    private String title;
}
