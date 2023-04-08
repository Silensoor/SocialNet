package socialnet.service.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.NotificationRq;
import socialnet.api.response.NotificationType;
import socialnet.exception.EmptyEmailException;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationsService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final NotificationRepository notificationRepository;

    public Object putNotification(String token, NotificationRq notificationRq) {
        String email = jwtUtils.getUserEmail(token);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            String notificationType = NotificationType.valueOf(notificationRq.getNotificationType()).toString();
            List<Notification> notifications = notificationRepository.getNotificationsByPersonIdAndNotificationType
                    (personsEmail.get(0).getId(), notificationType);
            notificationRepository.updateNotification(notificationRq.getEnable(),notifications.get(0).getId());
            return null;
        }

    }
}
