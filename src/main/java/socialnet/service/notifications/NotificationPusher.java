package socialnet.service.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import socialnet.api.response.NotificationRs;
import socialnet.api.response.PersonRs;
import socialnet.mappers.NotificationMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;

@Component
@Slf4j
public class NotificationPusher {

    private static SimpMessagingTemplate messagingTemplate;

    private static PersonRepository personRepository;

    private static NotificationRepository repository;


    public NotificationPusher(SimpMessagingTemplate simpMessagingTemplate, PersonRepository personRepository, NotificationRepository notificationRepository) {
        NotificationPusher.repository = notificationRepository;
        NotificationPusher.personRepository = personRepository;
        NotificationPusher.messagingTemplate = simpMessagingTemplate;
    }

    public static void sendPush(Notification notification, Long personId) {
        Long id = repository.saveNotification(notification);
        notification.setId(id);
        Person personReceiver = personRepository.findById(notification.getPersonId());
        PersonRs personRs = PersonMapper.INSTANCE.toDTO(personReceiver);
        NotificationRs dto = NotificationMapper.INSTANCE.toDTO(notification, personRs);
        try {
            messagingTemplate.convertAndSend(String.format("/user/%s/queue/notifications", personId),
                    dto);
        } catch (Exception e) {
            log.debug("exception in sending push notification!");
        }
    }


}
