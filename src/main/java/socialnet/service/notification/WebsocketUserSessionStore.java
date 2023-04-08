package socialnet.service.notification;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class WebsocketUserSessionStore {

    private final ConcurrentMap<String,String> store;

    public WebsocketUserSessionStore() {
        this.store = new ConcurrentHashMap<>();
    }

    void add(String session, String email) {
        store.put(session, email);
    }

    void remove(String session) {
        store.remove(session);
    }

    void removeByEmail(String email) {
        store.values().remove(email);
    }

    String getSessionIdByUserEmail(String email) {
        return store.entrySet().stream().filter(e -> e.getValue().equals(email)).map(Map.Entry::getKey).findFirst().orElse(null);
    }
}
