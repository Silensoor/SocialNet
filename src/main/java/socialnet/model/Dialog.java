package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Dialog {
    private Long id;

    private Long firstPersonId;

    private Long secondPersonId;

    private Timestamp lastActiveTime;

    private Long lastMessageId;
}
