package socialnet.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
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
    private final WebsocketUserSessionStore sessionStore;
    private final PersonRepository personRepository;

    public void sendPush(NotificationType notification, long userId) {
        Person personReceiver = personRepository.findById(userId);
        String sessionIdByUserEmail = sessionStore.getSessionIdByUserEmail(personReceiver.getEmail());
        if (sessionIdByUserEmail != null) {
            try {
                messagingTemplate.convertAndSendToUser(
                        sessionIdByUserEmail,
                        "/queue/notifications",
                        objectMapper.writeValueAsString(notification),
                        toMessageHeaders(sessionIdByUserEmail));
            } catch (Exception e) {
                log.debug("exception in sending push notification!");
            }
        }
    }

    private MessageHeaders toMessageHeaders(String session) {
        SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        simpMessageHeaderAccessor.setSessionId(session);
        simpMessageHeaderAccessor.setLeaveMutable(true);
        return simpMessageHeaderAccessor.getMessageHeaders();
    }
}
