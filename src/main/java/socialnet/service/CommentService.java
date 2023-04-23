package socialnet.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import socialnet.api.request.CommentRq;
import socialnet.api.response.*;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.NotificationPusher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final CommentMapper commentMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<CommentRs>> getComments(Long postId, Integer offset, Integer perPage, String jwtToken) {
        List<Comment> commentList = commentRepository.findByPostId(postId, offset, perPage);
        List<CommentRs> comments = new ArrayList<>();
        for (Comment comment : commentList) {
            if (comment.getIsDeleted()) continue;
            Details details = getToDTODetails(postId, comment, comment.getId());
            CommentRs commentRs = commentMapper.toDTO(comment, details);
            comments.add(commentRs);
        }
        comments = comments.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        int itemPerPage = offset / perPage;
        return new CommonRs<>(comments, itemPerPage, offset, perPage, System.currentTimeMillis(), (long) comments.size());
    }

    public CommonRs<CommentRs> createComment(CommentRq commentRq, Long postId, String jwtToken) {
        Person person = getPerson(jwtToken);
        CommentService.Details toModelDetails = getToModelDetails(postId, person);
        Comment comment = commentMapper.toModel(commentRq, toModelDetails);
        long commentId = commentRepository.save(comment);
        CommentService.Details toDTODetails = getToDTODetails(postId, comment, commentId);
        CommentRs commentRs = commentMapper.toDTO(comment, toDTODetails);

        Post post = postRepository.findById(postId.intValue());
        PersonSettings personSettingsPostAuthor = personSettingRepository.getPersonSettings(post.getAuthorId());
        if (commentRq.getParentId() != null) {
            Comment comment1 = commentRepository.findById(commentRq.getParentId().longValue());
            PersonSettings personSettingsCommentAuthor = personSettingRepository.getPersonSettings(comment1.getAuthorId());
            if (personSettingsCommentAuthor.getPostCommentNotification() &&
                    !person.getId().equals(comment1.getAuthorId())) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.COMMENT_COMMENT, comment1.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
            } else if (!person.getId().equals(post.getAuthorId()) && commentRq.getParentId().longValue() !=
                    (post.getAuthorId()) && personSettingsPostAuthor.getPostCommentNotification()) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
                return new CommonRs<>(commentRs, System.currentTimeMillis());
            }
        } else if (personSettingsPostAuthor.getPostCommentNotification() &&
                !post.getAuthorId().equals(person.getId())) {
            Notification notification = NotificationPusher.
                    getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
            NotificationPusher.sendPush(notification, person.getId());
        }
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    private Details getToDTODetails(Long postId, Comment comment, long commentId) {
        return new Details(new Timestamp(System.currentTimeMillis()), postId, comment.getIsBlocked(), comment.getIsDeleted(), commentId, comment.getAuthorId());
    }

    private Person getPerson(String jwtToken) {
        return personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
    }

    private Details getToModelDetails(Long postId, Person person) {
        PersonRs author = personMapper.toDTO(person);
        return new Details(author, postId);
    }

    public CommonRs<CommentRs> editComment(String jwtToken, Long id, Long commentId, CommentRq commentRq) {
        Person person = getPerson(jwtToken);
        CommentService.Details toModelDetails = getToModelDetails(id, person);
        Comment comment = commentMapper.toModel(commentRq, toModelDetails);
        Comment commentFromDB = commentRepository.findById(commentId);
        commentRepository.updateById(comment, commentId);
        commentFromDB.setCommentText(commentRq.getCommentText());
        Details toDTODetails = getToDTODetails(id, commentFromDB, commentId);
        CommentRs commentRs = commentMapper.toDTO(commentFromDB, toDTODetails);
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    public CommonRs<CommentRs> deleteComment(String jwtToken, Long id, Long commentId) {
        Comment commentFromDB = commentRepository.findById(commentId);
        commentFromDB.setIsDeleted(true);
        commentRepository.updateById(commentFromDB, commentId);
        CommentRs commentRs = commentMapper.toDTO(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void hardDeleteComments() {
        List<Comment> deletingComments = commentRepository.findDeletedPosts();
        commentRepository.deleteAll(deletingComments);
        List<Like> likes = new ArrayList<>();
        for (Comment deletingComment : deletingComments) {
            likes.addAll(likeRepository.getLikesByEntityId(deletingComment.getId()));
        }
        likeRepository.deleteAll(likes);
    }

    public CommonRs<CommentRs> recoverComment(String jwtToken, Long id, Long commentId) {
        Comment commentFromDB = commentRepository.findById(commentId);
        commentFromDB.setIsDeleted(false);
        commentRepository.updateById(commentFromDB, commentId);
        CommentRs commentRs = commentMapper.toDTO(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    @Data
    @NoArgsConstructor
    public class Details {
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

        public Details(Timestamp time, Long postId, Boolean isBlocked, Boolean isDeleted, Long id, Long authorId) {
            this.time = time;
            this.postId = postId;
            this.isBlocked = isBlocked;
            this.isDeleted = isDeleted;
            this.id = id;
            this.authorId = authorId;
            this.subComments = findSubComments(id);
            this.likes = likeRepository.getLikesByEntityId(id).size();
        }

        private List<CommentRs> findSubComments(Long id) {
            List<Comment> comments = commentRepository.findByPostIdParentId(id);
            List<CommentRs> commentRsList = new ArrayList<>();
            for (Comment comment : comments) {
                CommentRs commentRs = commentMapper.toDTO(comment, getToDTODetails(postId, comment, comment.getId()));
                commentRsList.add(commentRs);
            }
            return commentRsList;
        }

        public Details(PersonRs author, long postId) {
            this.subComments = new ArrayList<>();
            this.myLike = false;
            this.likes = 0;
            this.author = author;
            this.authorId = author.getId();
            this.myLike = false;
            this.isBlocked = false;
            this.isDeleted = false;
            this.postId = postId;
            this.time = new Timestamp(System.currentTimeMillis());

        }
    }
}
