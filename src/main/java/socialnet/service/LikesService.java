package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.api.response.NotificationType;
import socialnet.model.*;
import socialnet.repository.LikeRepository;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.notifications.NotificationPusher;

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
    private final NotificationRepository notificationRepository;

    public CommonRs<LikeRs> getLikes(String jwtToken, Integer itemId, String type) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        List<Like> likes = likeRepository.getLikesByEntityId(itemId);
        likes = likes.stream().filter(l -> l.getType().equals(type)).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        for (Like like : likes) {
            users.add(like.getPersonId().intValue());
        }
        return new CommonRs<>(new LikeRs(likes.size(), users), System.currentTimeMillis());
    }

    public CommonRs<LikeRs> putLike(String jwtToken, LikeRq likeRq) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        Like like = new Like();
        like.setType(likeRq.getType());
        like.setEntityId(likeRq.getItem_id().longValue());
        like.setPersonId(authUser.getId());
        int likeId = likeRepository.save(like);
        List<Like> likes = likeRepository.getLikesByEntityId(likeRq.getItem_id());
        likes = likes.stream().filter(l -> l.getType().equals(likeRq.getType())).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        for (Like l : likes) {
            users.add(l.getPersonId().intValue());
        }
        Post post = postRepository.findById(likeRq.getItem_id());
        List<PersonSettings> personSettings = notificationRepository.getPersonSettings(post.getAuthorId());
        if (!personSettings.isEmpty() && personSettings.get(0).getLikeNotification()) {
            Notification notification = NotificationPusher.getNotification(NotificationType.POST_LIKE,post.getAuthorId(),authUser.getId());
            NotificationPusher.sendPush(notification, authUser.getId());
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
        for (Like l : likesAfterDelete) {
            users.add(l.getPersonId().intValue());
        }
        return new CommonRs<>(new LikeRs(likesAfterDelete.size(), users), System.currentTimeMillis());
    }
}
