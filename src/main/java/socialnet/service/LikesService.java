package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.api.response.NotificationType;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.NotificationPusher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikeRepository likeRepository;
    private final PersonRepository personRepository;
    private final JwtUtils jwtUtils;
    private final PostRepository postRepository;
    private final PersonSettingRepository personSettingRepository;
    private final CommentRepository commentRepository;

    public CommonRs<LikeRs> getLikes (String jwtToken, Integer itemId, String type) {
        List<Like> likes = likeRepository.getLikesByEntityId(itemId);
        likes = likes.stream().filter(l -> l.getType().equals(type)).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));
        return new CommonRs<>(new LikeRs(likes.size(), users), System.currentTimeMillis());
    }

    public CommonRs<LikeRs> putLike(String jwtToken, LikeRq likeRq) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        Like like = new Like();
        like.setType(likeRq.getType());
        like.setEntityId(likeRq.getItem_id().longValue());
        like.setPersonId(authUser.getId());
        likeRepository.save(like);
        List<Like> likes = likeRepository.getLikesByEntityId(likeRq.getItem_id());
        likes = likes.stream().filter(l -> l.getType().equals(likeRq.getType())).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));

        for (Like l : likes) {
            users.add(l.getPersonId().intValue());
        }
        if (likeRq.getType().equals("Comment")) {
            Comment comment = commentRepository.findById(likeRq.getItem_id().longValue());
            PersonSettings personSettings = personSettingRepository.getSettings(comment.getAuthorId());

            if (personSettings.getPostLike() && !comment.getAuthorId().equals(authUser.getId())) {
                Notification notification = NotificationPusher.getNotification(NotificationType.POST_LIKE,
                        comment.getAuthorId(), authUser.getId());
                NotificationPusher.sendPush(notification, authUser.getId());
            }
        } else {
            Post post = postRepository.findById(likeRq.getItem_id());
            PersonSettings personSettings = personSettingRepository.getSettings(post.getAuthorId());
            if (personSettings.getPostLike() && !post.getAuthorId().equals(authUser.getId())) {
                Notification notification = NotificationPusher.getNotification(NotificationType.POST_LIKE,
                        post.getAuthorId(), authUser.getId());
                NotificationPusher.sendPush(notification, authUser.getId());
            }
        }
        return new CommonRs<>(new LikeRs(likes.size(), users), System.currentTimeMillis());
    }

    public CommonRs<LikeRs> deleteLike(String jwtToken, Integer itemId, String type) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        List<Like> likes = likeRepository.getLikesByEntityId(itemId);
        likes = likes.stream().filter(l -> l.getType().equals(type)).collect(Collectors.toList());
        for (Like like : likes) {
            if (like.getPersonId().equals(authUser.getId())) {
                likeRepository.delete(like);
                break;
            }
        }
        List<Like> likesAfterDelete = likeRepository.getLikesByEntityId(itemId);
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));
        return new CommonRs<>(new LikeRs(likesAfterDelete.size(), users), System.currentTimeMillis());
    }
}
