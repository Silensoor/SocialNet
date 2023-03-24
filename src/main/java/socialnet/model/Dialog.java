package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Dialog {
    private long id;
    private long firstPerson;
    private long secondPerson;
    private Timestamp lastActiveTime;
    private long lastMessageId;
}
