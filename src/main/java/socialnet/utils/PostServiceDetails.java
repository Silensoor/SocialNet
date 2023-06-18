package socialnet.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import socialnet.api.response.CommentRs;
import socialnet.model.Like;
import socialnet.model.Person;

import java.util.List;

@Data
@AllArgsConstructor
public class PostServiceDetails {
    Person author;
    List<Like> likes;
    List<String> tags;
    Long authUserId;
    List<CommentRs> comments;
}
