package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.api.response.NotificationType;
import socialnet.model.Like;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.repository.LikeRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.notifications.NotificationPusher;

import java.sql.Timestamp;
import java.time.Instant;
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

        Notification notification = getNotification(likeRq, authUser.getId());
        Post post = postRepository.findById(likeRq.getItem_id());
        NotificationPusher.sendPush(notification, post.getAuthorId());

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

    private Notification getNotification(LikeRq likeRq, Long personId) {
        Notification notification = new Notification();
        notification.setSentTime(Timestamp.from(Instant.now()));
        notification.setNotificationType(NotificationType.POST_LIKE.toString());
        notification.setContact(likeRq.getItem_id().toString());
        notification.setIsRead(false);
        notification.setPersonId(personId);
        notification.setEntityId(1L);
        return notification;
    }


}
