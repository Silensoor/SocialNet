package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Dialog {
    private Long id;
    private Long firstPerson;
    private Long secondPerson;
    private Timestamp lastActiveTime;
    private Long lastMessageId;
}
