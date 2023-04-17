package socialnet.service.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.NotificationRq;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.NotificationMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.model.PersonSettings;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationsService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final NotificationRepository notificationRepository;
    private final PersonMapper personMapper;
    private final NotificationMapper notificationMapper;

    public CommonRs<List<NotificationRs>> putNotifications(Boolean all, Integer notificationId, String token) {
        String email = jwtUtils.getUserEmail(token);
        List<Person> personList = personRepository.findPersonsEmail(email);
        if (personList == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        }
        List<NotificationRs> notificationRsList = new ArrayList<>();
        if (all) {
            Long personId = personList.get(0).getId();
            List<Notification> notifications = notificationRepository.getNotificationsByPersonId(personId);
            notificationRepository.updateIsReadAll(personId);
            for (Notification notification : notifications) {
                PersonRs personRs = PersonMapper.INSTANCE.toDTO(personList.get(0));
                NotificationRs notificationRs = NotificationMapper.INSTANCE.toDTO(notification, personRs);
                notificationRsList.add(notificationRs);
            }
        } else if(notificationId!=null) {
            List<Notification> notificationList = notificationRepository.getNotificationsById(notificationId);
            notificationRepository.updateIsReadById(notificationId);
            PersonRs personRs = PersonMapper.INSTANCE.toDTO(personList.get(0));
            NotificationRs notificationRs = NotificationMapper.INSTANCE.toDTO(notificationList.get(0), personRs);
            notificationRsList.add(notificationRs);
        }
        return getResponseNotifications(notificationRsList);
    }


    public CommonRs<List<NotificationRs>> getAllNotifications(Integer itemPerPage, String token, Integer offset) {
        String email = jwtUtils.getUserEmail(token);
        List<Person> personList = personRepository.findPersonsEmail(email);
        if (personList == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            Long id = personList.get(0).getId();
            List<Notification> notifications = notificationRepository.getNotifications(id, itemPerPage, offset);
            PersonRs personRs = personMapper.toDTO(personList.get(0));
            List<NotificationRs> rsList = new ArrayList<>();
            for (Notification notification : notifications) {
                rsList.add(notificationMapper.INSTANCE.toDTO(notification, personRs));
            }
            return getResponseNotifications(rsList);

        }
    }


    public CommonRs<List<PersonSettings>> getNotificationByPerson(String token) {
        String email = jwtUtils.getUserEmail(token);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            Long id = personsEmail.get(0).getId();
            List<PersonSettings> personSettings = notificationRepository.getPersonSettings(id);
            return getResponsePersonSettings(personSettings);
        }

    }

    public CommonRs<ComplexRs> putNotificationByPerson(String token, NotificationRq notificationRq) {
        String email = jwtUtils.getUserEmail(token);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            Long id = personsEmail.get(0).getId();
            String typeNotification = getTypeNotification(notificationRq.getNotificationType());
            notificationRepository.updatePersonSetting(notificationRq.getEnable(), typeNotification, id);
            return getResponseByPutTypeNotification();
        }

    }


    private String getTypeNotification(String notificationRq) {
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
        ComplexRs complexRs = new ComplexRs("string");
        CommonRs<ComplexRs> commonRs = new CommonRs<>(complexRs);
        commonRs.setTotal(500L);
        return commonRs;

    }

    private CommonRs<List<PersonSettings>> getResponsePersonSettings(List<PersonSettings> personSettings) {
        CommonRs<List<PersonSettings>> commonRs = new CommonRs<>(personSettings);
        commonRs.setTotal(500L);
        return commonRs;
    }

    private CommonRs<List<NotificationRs>> getResponseNotifications(List<NotificationRs> notificationRs) {
        CommonRs<List<NotificationRs>> commonRs = new CommonRs<>(notificationRs);
        commonRs.setTotal(500L);
        return commonRs;
    }

}
