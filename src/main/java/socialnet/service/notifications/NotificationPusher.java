package socialnet.service.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import socialnet.api.response.NotificationType;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPusher {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final PersonRepository personRepository;

    public void sendPush(NotificationType notification, long userId) {
        Person personReceiver = personRepository.findById(userId);
        try {
            messagingTemplate.convertAndSend(String.format("/user/%s/queue/notifications",personReceiver.getId()),
                    objectMapper.writeValueAsString(notification));
        } catch (Exception e) {
            log.debug("exception in sending push notification!");
        }
    }


}
