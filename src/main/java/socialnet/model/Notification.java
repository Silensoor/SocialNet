package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification {
    private long id;
    private String contact;
    private String notificationType;
    private long entityId;
    private boolean isRead;
    private Timestamp sentTime;
    private long personId;
}
