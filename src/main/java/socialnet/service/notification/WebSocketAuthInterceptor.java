package socialnet.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import socialnet.security.jwt.JwtUtils;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtUtils jwtUtils;
    private final WebsocketUserSessionStore sessionStore;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String sessionId = accessor.getMessageHeaders().get("simpSessionId").toString();

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("authorization");
            if (jwtUtils.validateJwtToken(jwtToken)) {
                String userEmail = jwtUtils.getUserEmail(jwtToken);
                sessionStore.add(sessionId, userEmail);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            sessionStore.remove(sessionId);
        }
        return message;
    }
}