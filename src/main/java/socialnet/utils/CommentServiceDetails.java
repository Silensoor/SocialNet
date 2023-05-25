package socialnet.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommentRs;
import socialnet.api.response.PersonRs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
@NoArgsConstructor
public class CommentServiceDetails {
    List<CommentRs> subComments;
    Boolean myLike;
    Integer likes;
    PersonRs author;
    Timestamp time;
    Long postId;
    Boolean isBlocked;
    Boolean isDeleted;
    Long id;
    Long authorId;

    public CommentServiceDetails (Timestamp time,
                                  Long postId,
                                  Boolean isBlocked,
                                  Boolean isDeleted,
                                  Long id,
                                  Long authorId,
                                  List<CommentRs> subComments,
                                  Integer likes) {
        this.time = time;
        this.postId = postId;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.id = id;
        this.authorId = authorId;
        this.subComments = subComments;
        this.likes = likes;
    }

//    private List<CommentRs> findSubComments(Long id) {
//        List<Comment> comments = commentRepository.findByPostIdParentId(id);
//        List<CommentRs> commentRsList = new ArrayList<>();
//        for (Comment comment : comments) {
//            CommentRs commentRs = commentMapper.toDTO(comment, commentService.getToDTODetails(postId, comment, comment.getId()));
//            commentRsList.add(commentRs);
//        }
//        return commentRsList;
//    }

    public CommentServiceDetails (PersonRs author,
                                  long postId) {
        this.subComments = new ArrayList<>();
        this.myLike = false;
        this.likes = 0;
        this.author = author;
        this.authorId = author.getId();
        this.isBlocked = false;
        this.isDeleted = false;
        this.postId = postId;
        this.time = new Timestamp(System.currentTimeMillis());

    }
}
