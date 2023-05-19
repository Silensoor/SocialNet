package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.CommonRs;
import socialnet.api.response.NotificationType;
import socialnet.api.response.PersonRs;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.CommentServiceDetails;
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
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<CommentRs>> getComments(Long postId, Integer offset, Integer perPage) {
        List<Comment> commentList = commentRepository.findByPostId(postId, offset, perPage);

        if (commentList == null) {
            return new CommonRs<>(new ArrayList<>(), perPage, offset, perPage, System.currentTimeMillis(), 0L);
        }

        List<CommentRs> comments = new ArrayList<>();

        for (Comment comment : commentList) {
            if (comment.getIsDeleted()) continue;
            CommentServiceDetails details = getToDTODetails(postId, comment, comment.getId());
            CommentRs commentRs = getCommentRs(comment, details);
            comments.add(commentRs);
        }

        comments = comments.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        Long total = commentRepository.countByPostId(postId);

        return new CommonRs<>(comments, perPage, offset, perPage, System.currentTimeMillis(), total);
    }

    private CommentRs getCommentRs(Comment comment, CommentServiceDetails details) {
        CommentRs commentRs = CommentMapper.INSTANCE.toDTO(comment);
        commentRs.setAuthor(details.getAuthor());
        commentRs.setLikes(details.getLikes());
        commentRs.setMyLike(details.getMyLike());
        commentRs.setSubComments(details.getSubComments());

        return commentRs;
    }


    public CommonRs<CommentRs> createComment(CommentRq commentRq, Long postId, String jwtToken) {
        Person person =getPerson(jwtToken);
        CommentServiceDetails toModelDetails = getToModelDetails(person,postId);
        Comment comment = getCommentModel(commentRq, toModelDetails);
        long commentId = commentRepository.save(comment);
        CommentServiceDetails toDTODetails = getToDTODetails(postId, comment, commentId);
        CommentRs commentRs = getCommentRs(comment, toDTODetails);

        Post post = postRepository.findById(postId.intValue());
        PersonSettings personSettingsPostAuthor = personSettingRepository.getSettings(post.getAuthorId());
        if (commentRq.getParentId() != null) {
            Comment comment1 = commentRepository.findById(commentRq.getParentId().longValue());
            PersonSettings personSettingsCommentAuthor = personSettingRepository.getSettings(comment1.getAuthorId());
            if (personSettingsCommentAuthor.getPostComment() &&
                    !person.getId().equals(comment1.getAuthorId())) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.COMMENT_COMMENT, comment1.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
            } else if (!person.getId().equals(post.getAuthorId()) && commentRq.getParentId().longValue() !=
                    (post.getAuthorId()) && personSettingsPostAuthor.getPostComment()) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
                return new CommonRs<>(commentRs, System.currentTimeMillis());
            }
        } else if (personSettingsPostAuthor.getPostComment() &&
                !post.getAuthorId().equals(person.getId())) {
            Notification notification = NotificationPusher.
                    getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
            NotificationPusher.sendPush(notification, person.getId());
        }
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }


    private Comment getCommentModel(CommentRq commentRq, CommentServiceDetails details) {
        Comment comment = CommentMapper.INSTANCE.toModel(commentRq);
        comment.setAuthorId(details.getAuthorId());
        comment.setIsBlocked(details.getIsBlocked());
        comment.setIsDeleted(details.getIsDeleted());
        comment.setPostId(details.getPostId());
        comment.setTime(details.getTime());
        comment.setId(details.getId());

        return comment;
    }

    public CommentServiceDetails getToDTODetails(Long postId, Comment comment, long commentId) {
        return new CommentServiceDetails(new Timestamp(System.currentTimeMillis()),
                postId,
                comment.getIsBlocked(),
                comment.getIsDeleted(),
                commentId, comment.getAuthorId(),
                findSubComments(postId, commentId),
                likeRepository.getLikesByEntityId(commentId).size());
    }

    private List<CommentRs> findSubComments(Long postId, Long id) {
        List<Comment> comments = commentRepository.findByPostIdParentId(id);
        List<CommentRs> commentRsList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentRs commentRs = getCommentRs(comment, getToDTODetails(postId, comment, comment.getId()));
            commentRsList.add(commentRs);
        }
        return commentRsList;
    }

    private Person getPerson(String jwtToken) {
       return personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));

    }
    private CommentServiceDetails getToModelDetails(Person person,Long postId){
        PersonRs author = PersonMapper.INSTANCE.toDTO(person);
        return new CommentServiceDetails(author, postId);
    }

    public CommonRs<CommentRs> editComment(String jwtToken, Long id, Long commentId, CommentRq commentRq) {
        Person person = getPerson(jwtToken);
        CommentServiceDetails toModelDetails = getToModelDetails(person, id);
        Comment comment = getCommentModel(commentRq, toModelDetails);
        Comment commentFromDB = commentRepository.findById(commentId);
        commentRepository.updateById(comment, commentId);
        commentFromDB.setCommentText(commentRq.getCommentText());
        CommentServiceDetails toDTODetails = getToDTODetails(id, commentFromDB, commentId);
        CommentRs commentRs = getCommentRs(commentFromDB, toDTODetails);
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    public CommonRs<CommentRs> deleteComment(String jwtToken, Long id, Long commentId) {
        Comment commentFromDB = commentRepository.findById(commentId);
        commentFromDB.setIsDeleted(true);
        commentRepository.updateById(commentFromDB, commentId);
        CommentRs commentRs = getCommentRs(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }
    public void hardDeleteComments() {
        List<Comment> deletingComments = commentRepository.findDeletedPosts();
        deletingComments.forEach(commentRepository::delete);
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
        CommentRs commentRs = getCommentRs(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

//    @Data
//    @NoArgsConstructor
//    public class Details {
//        List<CommentRs> subComments;
//        Boolean myLike;
//        Integer likes;
//        PersonRs author;
//        Timestamp time;
//        Long postId;
//        Boolean isBlocked;
//        Boolean isDeleted;
//        Long id;
//        Long authorId;
//
//        public Details(Timestamp time, Long postId, Boolean isBlocked, Boolean isDeleted, Long id, Long authorId) {
//            this.time = time;
//            this.postId = postId;
//            this.isBlocked = isBlocked;
//            this.isDeleted = isDeleted;
//            this.id = id;
//            this.authorId = authorId;
//            this.subComments = findSubComments(id);
//            this.likes = likeRepository.getLikesByEntityId(id).size();
//        }
//
//        private List<CommentRs> findSubComments(Long id) {
//            List<Comment> comments = commentRepository.findByPostIdParentId(id);
//            List<CommentRs> commentRsList = new ArrayList<>();
//            for (Comment comment : comments) {
//                CommentRs commentRs = commentMapper.toDTO(comment, getToDTODetails(postId, comment, comment.getId()));
//                commentRsList.add(commentRs);
//            }
//            return commentRsList;
//        }
//
//        public Details(PersonRs author, long postId) {
//            this.subComments = new ArrayList<>();
//            this.myLike = false;
//            this.likes = 0;
//            this.author = author;
//            this.authorId = author.getId();
//            this.myLike = false;
//            this.isBlocked = false;
//            this.isDeleted = false;
//            this.postId = postId;
//            this.time = new Timestamp(System.currentTimeMillis());
//
//        }
//    }
}
