package socialnet.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostRq {
    private String post_text;
    private List<String> tags;
    private String title;
}
