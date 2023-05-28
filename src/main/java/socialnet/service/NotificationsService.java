package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.NotificationRq;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.NotificationMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PersonSettingRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationsService {
    private final JwtUtils jwtUtils;

    private final PersonRepository personRepository;
    private final NotificationRepository notificationRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<NotificationRs>> putNotifications(Boolean all, Integer notificationId, String token) {
        String email = jwtUtils.getUserEmail(token);
        Person personList = personRepository.findByEmail(email);
        if (personList == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        }
        List<NotificationRs> notificationRsList = new ArrayList<>();
        if (all!=null&&all) {
            Long personId = personList.getId();
            List<Notification> notifications = notificationRepository.getNotificationsByPersonId(personId);
            notificationRepository.updateIsReadAll(personId);
            for (Notification notification : notifications) {
                PersonRs personRs = PersonMapper.INSTANCE.toDTO(personList);
                NotificationRs notificationRs = NotificationMapper.INSTANCE.toDTO(notification);
                notificationRs.setEntityAuthor(personRs);
                notificationRsList.add(notificationRs);
            }
        } else if (notificationId != null) {
            List<Notification> notificationList = notificationRepository.getNotificationsById(notificationId);
            notificationRepository.updateIsReadById(notificationId);
            PersonRs personRs = PersonMapper.INSTANCE.toDTO(personList);
            NotificationRs notificationRs = NotificationMapper.INSTANCE.toDTO(notificationList.get(0));
            notificationRs.setEntityAuthor(personRs);
            notificationRsList.add(notificationRs);
        }
        return getResponseNotifications(notificationRsList);
    }


    public CommonRs<List<NotificationRs>> getAllNotifications(Integer itemPerPage, String token, Integer offset) {
        String email = jwtUtils.getUserEmail(token);
        Person person = personRepository.findByEmail(email);
        if (person == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            Long id = person.getId();
            List<Notification> notifications = notificationRepository.getNotifications(id, itemPerPage, offset);
            if (!notifications.isEmpty()) {
                PersonRs personRs = PersonMapper.INSTANCE.toDTO(personRepository.findById(notifications.get(0).getEntityId()));
                List<NotificationRs> rsList = new ArrayList<>();
                for (Notification notification : notifications) {
                    NotificationRs notificationRs = NotificationMapper.INSTANCE.toDTO(notification);
                    notificationRs.setEntityAuthor(personRs);
                    rsList.add(notificationRs);
                }

                CommonRs<List<NotificationRs>> result = getResponseNotifications(rsList);
                result.setTotal(notificationRepository.countNotifications(id));

                return result;
            } else {
                List<NotificationRs> rsList = new ArrayList<>();
                return getResponseNotifications(rsList);
            }
        }
    }


    public CommonRs<ComplexRs> putNotificationByPerson(String token, NotificationRq notificationRq) {
        String email = jwtUtils.getUserEmail(token);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            Long id = personsEmail.getId();
            String typeNotification = getSqlFieldName(notificationRq.getNotificationType());
            personSettingRepository.updatePersonSetting(notificationRq.getEnable(), typeNotification, id);
            return getResponseByPutTypeNotification();
        }
    }

    private String getSqlFieldName(String notificationRq) {
        switch (NotificationType.valueOf(notificationRq)) {
            case POST:
                return "post_notification";
            case POST_COMMENT:
                return "post_comment_notification";
            case POST_LIKE:
                return "like_notification";
            case COMMENT_COMMENT:
                return "comment_comment_notification";
            case FRIEND_BIRTHDAY:
                return "friend_birthday_notification";
            case FRIEND_REQUEST:
                return "friend_request";
            case MESSAGE:
                return "message_Notification";
        }
        return null;
    }

    private CommonRs<ComplexRs> getResponseByPutTypeNotification() {
        return new CommonRs<>(new ComplexRs("string"));
    }

    private CommonRs<List<NotificationRs>> getResponseNotifications(List<NotificationRs> notificationRs) {
        return new CommonRs<>(notificationRs);
    }
}
