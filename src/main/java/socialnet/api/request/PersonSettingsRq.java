package socialnet.api.request;

import lombok.Data;

@Data
public class PersonSettingsRq {
    private Long id;
    private Boolean commentComment;
    private Boolean friendBirthday;
    private Boolean friendRequest;
    private Boolean postLike;
    private Boolean message;
    private Boolean postComment;
    private Boolean post;
}