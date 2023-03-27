package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.dto.CommentRs;
import socialnet.model.Comment;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final PersonRepository personRepository;
    private final PersonService personService;

    public CommentRs convertToCommentRs(Comment comment) {
        CommentRs commentRs = new CommentRs();
        Person author = personRepository.findById(comment.getAuthorId());
        commentRs.setAuthor(personService.convertToPersonRs(author));
        commentRs.setCommentText(comment.getCommentText());
        commentRs.setId(comment.getId());
        commentRs.setIsBlocked(comment.getIsBlocked());
        commentRs.setIsDeleted(comment.getIsDeleted());
        commentRs.setLikes(null);
        commentRs.setMyLike(null);
        commentRs.setParentId(comment.getParentId());
        commentRs.setPostId(comment.getPostId());
        commentRs.setSubComments(null);
        commentRs.setTime(comment.getTime().toString());

        return commentRs;
    }
}
